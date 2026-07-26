package dev.exefile7f.rheniumcore;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.minecraft.entity.ai.brain.sensor.Sensor.testAttackableTargetPredicate;
import static net.minecraft.entity.ai.brain.sensor.Sensor.testTargetPredicate;

public interface StaticResource{
    int CPU_CORES = getCores();
    ThreadPool THREAD_POOL = new ThreadPool();
    Path CONFIG_PATH = getConfigPath();

    //Task Status
    byte NONE = 0;
    byte CALCULATING = 1;
    byte FINISHED = 2;

    //Tick Pool Status
    byte STOP = - 1;
    byte NO_TASK = 0;
    byte HAVE_TASK = 1;

    int COMPUTE_SIZE = 5;
    List<Consumer<Tasks.Task>> COMPUTE_FUNCTIONS = COMPUTE_FUNCTIONS();
    List<Consumer<Tasks.Task>> WRITE_FUNCTIONS = WRITE_FUNCTIONS();

    static List<Consumer<Tasks.Task>> COMPUTE_FUNCTIONS(){
        List<Consumer<Tasks.Task>> t = fillList(new ArrayList<>(), COMPUTE_SIZE);
        t.set(0, (s) -> {//NearestPlayersSensor
            ServerWorld world = (ServerWorld) s.input[1];
            LivingEntity entity = (LivingEntity) s.input[2];
            List<PlayerEntity> list = world.getPlayers()
                    .stream()
                    .filter(EntityPredicates.EXCEPT_SPECTATOR)
                    .filter(player -> entity.isInRange(player, entity.getAttributeValue(EntityAttributes.FOLLOW_RANGE)))
                    .sorted(Comparator.comparingDouble(entity::squaredDistanceTo))
                    .collect(Collectors.toList());
            s.output[0] = MemoryModuleType.NEAREST_PLAYERS;
            s.output[1] = list;
            List<PlayerEntity> list2 = list.stream().filter(player -> testTargetPredicate(world, entity, player)).collect(Collectors.toList());
            s.output[2] = MemoryModuleType.NEAREST_VISIBLE_PLAYER;
            s.output[3] = list2.isEmpty() ? null : list2.get(0);
            List<PlayerEntity> list3 = list2.stream().filter(player -> testAttackableTargetPredicate(world, entity, player)).toList();
            s.output[4] = MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYERS;
            s.output[5] = list3;
            s.output[6] = MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER;
            s.output[7] = list3.isEmpty() ? null : list3.get(0);
        });
        t.set(1, (s) -> {//

        });
        t.set(2, (s) -> {//

        });
        t.set(3, (s) -> {//

        });
        t.set(4, (s) -> {//

        });
        return t;
    }

    static List<Consumer<Tasks.Task>> WRITE_FUNCTIONS(){
        List<Consumer<Tasks.Task>> t = fillList(new ArrayList<>(), COMPUTE_SIZE);
        return t;
    }

    static int getCores(){
        return Runtime.getRuntime().availableProcessors();
    }

    static Path getConfigPath(){
        return FabricLoader.getInstance().getConfigDir();
    }

    static <T> List<T> fillList(List<T> t, int size){
        t.clear();
        for(int i = 0; i < size; i++){
            t.add(null);
        }
        return t;
    }
}
