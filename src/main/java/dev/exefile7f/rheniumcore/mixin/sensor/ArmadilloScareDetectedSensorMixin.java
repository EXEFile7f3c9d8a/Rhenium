package dev.exefile7f.rheniumcore.mixin.sensor;

import dev.exefile7f.rheniumcore.threadpool.Tasks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.ArmadilloScareDetectedSensor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiPredicate;

import static dev.exefile7f.rheniumcore.RheniumCore.THREAD_POOL;
import static dev.exefile7f.rheniumcore.statics.StaticResource.ARMADILLO_SCARE_DETECTED_SENSOR;

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
        Tasks tasks = THREAD_POOL.tasks;
        Tasks.Task task = tasks.getNearestEmptyTask();
        task.putInput(ci)
                .putInput(entity)
                .putInput(this.threateningEntityPredicate)
                .putInput(this.memoryModuleType)
                .putInput(expiry)
                .setComputeType(ARMADILLO_SCARE_DETECTED_SENSOR);
        tasks.addTask(task);
        ci.cancel();
    }
}