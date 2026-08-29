package dev.exefile7f.rheniumcore.mixin.sensor;

import com.google.common.collect.Lists;
import dev.exefile7f.rheniumcore.RheniumCore;
import dev.exefile7f.rheniumcore.util.threadpool.Tasks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.HoglinSpecificSensor;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

import static dev.exefile7f.rheniumcore.RheniumCore.THREAD_POOL;

@Mixin(HoglinSpecificSensor.class)
public class HoglinSpecificSensorMixin{
    @Inject(
            method = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/HoglinEntity;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void sense(ServerWorld serverWorld, HoglinEntity hoglinEntity, CallbackInfo ci){
        RheniumCore.MIXIN_REG.reg(HoglinSpecificSensorMixin.class, (s) -> {
            HoglinEntity ent = (HoglinEntity)s.input[2];
            ServerWorld serverWord = (ServerWorld)s.input[1];
            Brain<?> brain = ent.getBrain();
            s.putOutputs(MemoryModuleType.NEAREST_REPELLENT,
                    BlockPos.findClosest(
                            ent.getBlockPos(),
                            8,
                            4,
                            (pos) -> serverWord.getBlockState(pos).isIn(BlockTags.HOGLIN_REPELLENTS)
                    )
            );
            Optional<PiglinEntity> optional = Optional.empty();
            int i = 0;
            List<HoglinEntity> list = Lists.newArrayList();
            LivingTargetCache livingTargetCache = brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty());
            for(LivingEntity livingEntity :
                    livingTargetCache.iterate((livingEntityx) ->
                            !livingEntityx.isBaby() && (livingEntityx instanceof PiglinEntity || livingEntityx instanceof HoglinEntity)
                    )
            ){
                if(livingEntity instanceof PiglinEntity piglinEntity){
                    ++i;
                    if(optional.isEmpty()){
                        optional = Optional.of(piglinEntity);
                    }
                }
                if(livingEntity instanceof HoglinEntity hoglinEntity2){
                    list.add(hoglinEntity2);
                }
            }
            s.putOutputs(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, optional);
            s.putOutputs(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, list);
            s.putOutputs(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, i);
            s.putOutputs(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, list.size());
        }, (s) -> {
            HoglinEntity ent = (HoglinEntity)s.input[2];
            Brain<?> brain = ent.getBrain();
            brain.remember((MemoryModuleType<BlockPos>)s.output[0], (Optional<BlockPos>)s.output[1]);
            brain.remember((MemoryModuleType<AbstractPiglinEntity>)s.output[2], (Optional<PiglinEntity>)s.output[3]);
            brain.remember((MemoryModuleType<List<HoglinEntity>>)s.output[4], (List<HoglinEntity>)s.output[5]);
            brain.remember((MemoryModuleType<Integer>)s.output[6], (int)s.output[7]);
            brain.remember((MemoryModuleType<Integer>)s.output[8], (int)s.output[9]);
        });
        Tasks tasks = THREAD_POOL.tasks;
        tasks.addTask(tasks.getNearestEmptyTask().putInputs(ci, serverWorld, hoglinEntity).setComputeType(HoglinSpecificSensorMixin.class));
        ci.cancel();
    }
}
