package dev.exefile7f.rheniumcore.mixin;

import dev.exefile7f.rheniumcore.Tasks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.NearestPlayersSensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

import static dev.exefile7f.rheniumcore.StaticResource.*;

public final class Mixins{
    @Mixin(ServerWorld.class)
    public static abstract class ServerWorldMixin{
        @Shadow public abstract void cacheStructures(Chunk chunk);

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
            THREAD_POOL.tasks.writeAll(WRITE_FUNCTIONS, THREAD_POOL);
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
                tasks.addTask(task);
                ci.cancel();
            }
        }
    }
}