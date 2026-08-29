package dev.exefile7f.rheniumcore.mixin.sensor;

import dev.exefile7f.rheniumcore.RheniumCore;
import dev.exefile7f.rheniumcore.util.threadpool.Tasks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static dev.exefile7f.rheniumcore.RheniumCore.THREAD_POOL;

@Mixin(NearestLivingEntitiesSensor.class)
public class NearestLivingEntitiesSensorMixin<T extends LivingEntity>{
    @Inject(
            method = "sense",
            at = @At("HEAD"),
            cancellable = true
    )
    public void sense(ServerWorld world, T entity, CallbackInfo ci){
        RheniumCore.MIXIN_REG.reg(this.getClass(), (s) -> {
            ServerWorld word = (ServerWorld)s.input[1];
            T ent = (T)s.input[2];
            double d = ent.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
            Box box = ent.getBoundingBox().expand(d, d, d);
            List<LivingEntity> list = word.getEntitiesByClass(LivingEntity.class, box, (e) -> e != ent && e.isAlive());
            Objects.requireNonNull(ent);
            list.sort(Comparator.comparingDouble(ent::squaredDistanceTo));
            s.putOutputs(ent.getBrain(), MemoryModuleType.MOBS, list, MemoryModuleType.VISIBLE_MOBS, new LivingTargetCache(world, entity, list));
        }, (s) -> {
            Brain<?> ent = (Brain<?>)s.output[0];
            ent.remember((MemoryModuleType<List<LivingEntity>>)s.output[1], (List<LivingEntity>)s.output[2]);
            ent.remember((MemoryModuleType<LivingTargetCache>)s.output[3], (LivingTargetCache)s.output[4]);
        });
        Tasks tasks = THREAD_POOL.tasks;
        tasks.addTask(tasks.getNearestEmptyTask().putInputs(ci, world, entity).setComputeType(this.getClass()));
        ci.cancel();
    }
}
