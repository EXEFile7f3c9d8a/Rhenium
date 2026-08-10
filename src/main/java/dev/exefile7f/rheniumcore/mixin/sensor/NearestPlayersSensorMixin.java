package dev.exefile7f.rheniumcore.mixin.sensor;

import dev.exefile7f.rheniumcore.Tasks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.NearestPlayersSensor;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.exefile7f.rheniumcore.RheniumCore.THREAD_POOL;
import static dev.exefile7f.rheniumcore.StaticResource.NEAREST_PLAYER_SENSOR;

@Mixin(NearestPlayersSensor.class)
public final class NearestPlayersSensorMixin{

    @Inject(method = "sense", at = @At("HEAD"), cancellable = true)
    public void sense(ServerWorld world, LivingEntity entity, CallbackInfo ci){
        Tasks tasks = THREAD_POOL.tasks;
        Tasks.Task task = tasks.getNearestEmptyTask();
        task.putInput(ci)
                .putInput(world)
                .putInput(entity)
                .setComputeType(NEAREST_PLAYER_SENSOR);
        tasks.addTask(task);
        ci.cancel();
    }
}