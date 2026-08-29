package dev.exefile7f.rheniumcore.mixin.sensor;

import dev.exefile7f.rheniumcore.RheniumCore;
import dev.exefile7f.rheniumcore.util.threadpool.Tasks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.ArmadilloScareDetectedSensor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

import static dev.exefile7f.rheniumcore.RheniumCore.THREAD_POOL;

@Mixin(ArmadilloScareDetectedSensor.class)
public final class ArmadilloScareDetectedSensorMixin<T extends LivingEntity>{
    @Final
    @Shadow
    private BiPredicate<T, LivingEntity> threateningEntityPredicate;
    @Final
    @Shadow
    private MemoryModuleType<Boolean> memoryModuleType;
    @Final
    @Shadow
    private int expiry;

    @Inject(
            method = "tryDetectThreat",
            at = @At("HEAD"),
            cancellable = true
    )
    public void tryDetectThreat(T entity, CallbackInfo ci){
        RheniumCore.MIXIN_REG.reg(this.getClass(), (s) -> {
            LivingEntity ent = (LivingEntity)s.input[1];
            Optional<List<LivingEntity>> optional = ent.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS);
            if(!optional.isEmpty()){
                boolean bl = ((List)optional.get()).stream().anyMatch((threat) -> ((BiPredicate<LivingEntity, LivingEntity>)s.input[2]).test(ent, (LivingEntity)threat));
                if(bl){
                    s.putOutputs(true, ent, s.input[3], s.input[4]);
                }else{
                    s.putOutput(false);
                }
            }
        }, (s) -> {
            if((boolean)s.output[0]){
                ((LivingEntity)s.output[1]).getBrain().remember((MemoryModuleType<Boolean>)s.output[2], true, (long)s.output[3]);
            }
        });
        Tasks tasks = THREAD_POOL.tasks;
        tasks.addTask(tasks.getNearestEmptyTask()
                           .putInputs(ci, entity, this.threateningEntityPredicate, this.memoryModuleType, expiry)
                           .setComputeType(this.getClass())
        );
        ci.cancel();
    }
}