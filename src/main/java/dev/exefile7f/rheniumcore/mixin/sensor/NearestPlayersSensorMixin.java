package dev.exefile7f.rheniumcore.mixin.sensor;

import dev.exefile7f.rheniumcore.RheniumCore;
import dev.exefile7f.rheniumcore.util.threadpool.Tasks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestPlayersSensor;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dev.exefile7f.rheniumcore.RheniumCore.THREAD_POOL;
import static net.minecraft.entity.ai.brain.sensor.Sensor.testAttackableTargetPredicate;
import static net.minecraft.entity.ai.brain.sensor.Sensor.testTargetPredicate;

@Mixin(NearestPlayersSensor.class)
public final class NearestPlayersSensorMixin{

    @Inject(
            method = "sense",
            at = @At("HEAD"),
            cancellable = true
    )
    public void sense(ServerWorld world, LivingEntity entity, CallbackInfo ci){
        RheniumCore.MIXIN_REG.reg(this.getClass(), (s) -> {
            ServerWorld word = (ServerWorld) s.input[1];
            LivingEntity ent = (LivingEntity) s.input[2];
            List<PlayerEntity> list = word.getPlayers()
                                          .stream()
                                          .filter(EntityPredicates.EXCEPT_SPECTATOR)
                                          .filter(player -> ent.isInRange(player, ent.getAttributeValue(EntityAttributes.FOLLOW_RANGE)))
                                          .sorted(Comparator.comparingDouble(ent::squaredDistanceTo))
                                          .collect(Collectors.toList());
            s.putOutputs(MemoryModuleType.NEAREST_PLAYERS, list);
            List<PlayerEntity> list2 = list.stream().filter(player -> testTargetPredicate(word, ent, player)).collect(Collectors.toList());
            s.putOutputs(MemoryModuleType.NEAREST_VISIBLE_PLAYER, list2.isEmpty() ? null : list2.get(0));
            List<PlayerEntity> list3 = list2.stream().filter(player -> testAttackableTargetPredicate(word, ent, player)).toList();
            s.putOutputs(
                    MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYERS,
                    list3,
                    MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
                    list3.isEmpty() ? null : list3.get(0))
            ;
        }, (s) -> {
            Brain<?> brain = ((LivingEntity)s.input[2]).getBrain();
            brain.remember((MemoryModuleType<List<PlayerEntity>>)s.output[0], (List<PlayerEntity>)s.output[1]);
            brain.remember((MemoryModuleType<PlayerEntity>)s.output[2], (PlayerEntity)s.output[3]);
            brain.remember((MemoryModuleType<List<PlayerEntity>>)s.output[4], (List<PlayerEntity>)s.output[5]);
            brain.remember((MemoryModuleType<PlayerEntity>)s.output[6], (PlayerEntity)s.output[7]);
        });
        Tasks tasks = THREAD_POOL.tasks;
        tasks.addTask(tasks.getNearestEmptyTask().putInputs(ci, world, entity).setComputeType(this.getClass()));
        ci.cancel();
    }
}