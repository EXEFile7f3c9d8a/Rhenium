package dev.exefile7f.rheniumcore.mixin;

import dev.exefile7f.rheniumcore.Tasks;
import dev.exefile7f.rheniumcore.ThreadPool;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.ArmadilloScareDetectedSensor;
import net.minecraft.entity.ai.brain.sensor.NearestPlayersSensor;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import static dev.exefile7f.rheniumcore.StaticResource.*;

public final class Mixins{
    public static ThreadPool THREAD_POOL = new ThreadPool();

    @Mixin(ServerWorld.class)
    public static final class ServerWorldMixin{
        @Inject(
                method = "tick",
                at = @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/world/EntityList;forEach(Ljava/util/function/Consumer;)V",
                        ordinal = 0,
                        shift = At.Shift.AFTER
                )
        )
        public void entitiesTickWrite(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
            THREAD_POOL.launch();
            try{
                THREAD_POOL.tasks.wait();
            }catch(InterruptedException e){
                throw new RuntimeException(e);
            }
            THREAD_POOL.tasks.taskAll(WRITE_FUNCTIONS, THREAD_POOL, THREAD_POOL.tasks.writeCounter);
        }
    }

    public static final class SensorsMixins{
        @Mixin(NearestPlayersSensor.class)
        public static final class NearestPlayersSensorMixin{
            @Inject(method = "sense", at = @At("HEAD"), cancellable = true)
            public void sense(ServerWorld world, LivingEntity entity, CallbackInfo ci){
                Tasks tasks = THREAD_POOL.tasks;
                Tasks.Task task = tasks.getNearestEmptyTask();
                task.input[0] = ci;
                task.input[1] = world;
                task.input[2] = entity;
                task.computeType = 0;
                tasks.addTask(task);
                ci.cancel();
            }
        }

        @Mixin(ArmadilloScareDetectedSensor.class)
        public static final class ArmadilloScareDetectedSensorMixins<T extends LivingEntity>{
            @Final @Shadow
            private BiPredicate<T, LivingEntity> threateningEntityPredicate;
            @Final @Shadow
            private Predicate<T> canRollUpPredicate;
            @Final @Shadow
            private MemoryModuleType<Boolean> memoryModuleType;
            @Final @Shadow
            private int expiry;

            @Inject(
                    method = "tryDetectThreat",
                    at = @At("HEAD"),
                    cancellable = true)
            public void tryDetectThreat(T entity, CallbackInfo ci){
                Optional<List<LivingEntity>> optional = entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS);
                if(! optional.isEmpty()){
                    boolean bl = ((List) optional.get()).stream().anyMatch((threat) -> this.threateningEntityPredicate.test(entity, (LivingEntity)threat));
                    if(bl){
                        this.onDetected(entity);
                    }
                }
                ci.cancel();
            }
            @Shadow
            public void onDetected(T entity) {
                entity.getBrain().remember(this.memoryModuleType, true, (long)this.expiry);
            }
        }
    }
}