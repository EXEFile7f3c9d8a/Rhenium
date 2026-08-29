package dev.exefile7f.rheniumcore.mixin.server;

import dev.exefile7f.rheniumcore.RheniumCore;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

import static dev.exefile7f.rheniumcore.RheniumCore.THREAD_POOL;

@Mixin(ServerWorld.class)
public final class ServerWorldMixin{
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
        THREAD_POOL.tasks.taskAll(RheniumCore.MIXIN_REG.getOutputMethods(), THREAD_POOL, THREAD_POOL.tasks.writeCounter);
    }
}