package dev.exefile7f.rheniumcore.mixin;

import dev.exefile7f.rheniumcore.Tasks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.NearestPlayersSensor;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

import static dev.exefile7f.rheniumcore.StaticResource.*;

public class Mixins{
    @Mixin(ServerWorld.class)
    public static class ServerWorldMixin{
        @Inject(
                method = "tick",
                at = @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
                        ordinal = 6,
                        shift = At.Shift.BEFORE
                )
        )
        public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci){

        }

    }

    public static class SensorsMixins{
        @Mixin(NearestPlayersSensor.class)
        public static class NearestPlayersSensorMixin{
            @Inject(method = "sense", at = @At("HEAD"), cancellable = true)
            public void sense(ServerWorld world, LivingEntity entity, CallbackInfo ci){
                Tasks tasks = THREAD_POOL.tasks;
                Tasks.Task task = new Tasks.Task();
                task.input[0] = ci;
                task.input[1] = world;
                task.input[2] = entity;
                tasks.tasks.set(tasks.size.getAndIncrement(), task);
                ci.cancel();
            }
        }
    }
}