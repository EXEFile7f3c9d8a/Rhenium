package dev.exefile7f.rheniumcore.statics;

import dev.exefile7f.rheniumcore.RheniumCore;
import dev.exefile7f.rheniumcore.threadpool.Tasks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.minecraft.entity.ai.brain.sensor.Sensor.*;

public interface StaticResource{
    int CPU_CORES = getCores();
    Path CONFIG_PATH = getConfigPath();

    //Task Status
    int NONE = 0;
    int CALCULATING = 1;
    int FINISHED = 2;

    //Tick Pool Status
    int STOP = - 1;
    int NO_TASK = 0;
    int HAVE_TASK = 1;

    int COMPUTE_SIZE = 5;
    List<Consumer<Tasks.Task>> COMPUTE_FUNCTIONS = COMPUTE_FUNCTIONS();
    List<Consumer<Tasks.Task>> WRITE_FUNCTIONS = WRITE_FUNCTIONS();

    int NEAREST_PLAYER_SENSOR = 0;
    int ARMADILLO_SCARE_DETECTED_SENSOR = 1;
    int NEAREST_LIVING_ENTITIES_SENSOR = 2;
    static List<Consumer<Tasks.Task>> COMPUTE_FUNCTIONS(){
        List<Consumer<Tasks.Task>> t = fillList(new ArrayList<>(), COMPUTE_SIZE);
        t.set(NEAREST_PLAYER_SENSOR, (s) -> {
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
        t.set(ARMADILLO_SCARE_DETECTED_SENSOR, (s) -> {
            LivingEntity entity = (LivingEntity)s.input[1];
            Optional<List<LivingEntity>> optional = entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS);
            if(! optional.isEmpty()){
                boolean bl = ((List) optional.get()).stream().anyMatch((threat) -> ((BiPredicate<LivingEntity, LivingEntity>)s.input[2]).test(entity, (LivingEntity)threat));
                if(bl){
                    s.output[0] = true;
                    s.output[1] = entity;
                    s.output[2] = s.input[3];
                    s.output[3] = s.input[4];
                }else{
                    s.output[0] = false;
                }
            }
        });
        t.set(2, (s) -> {

        });
        t.set(3, (s) -> {

        });
        t.set(4, (s) -> {

        });
        return t;
    }

    static List<Consumer<Tasks.Task>> WRITE_FUNCTIONS(){
        List<Consumer<Tasks.Task>> t = fillList(new ArrayList<>(), COMPUTE_SIZE);
        t.set(NEAREST_PLAYER_SENSOR, (s) -> {
            Brain<?> brain = ((LivingEntity)s.input[2]).getBrain();
            brain.remember((MemoryModuleType<List<PlayerEntity>>)s.output[0], (List<PlayerEntity>)s.output[1]);
            brain.remember((MemoryModuleType<PlayerEntity>)s.output[2], (PlayerEntity)s.output[3]);
            brain.remember((MemoryModuleType<List<PlayerEntity>>)s.output[4], (List<PlayerEntity>)s.output[5]);
            brain.remember((MemoryModuleType<PlayerEntity>)s.output[6], (PlayerEntity)s.output[7]);
        });
        t.set(ARMADILLO_SCARE_DETECTED_SENSOR, (s) -> {
            if((boolean)s.output[0]){
                ((LivingEntity)s.output[1]).getBrain().remember((MemoryModuleType<Boolean>)s.output[2], true, (long)s.output[3]);
            }
        });
        t.set(2, (s) -> {

        });
        t.set(3, (s) -> {

        });
        t.set(4, (s) -> {

        });
        return t;
    }

    static int getCores(){
        return Runtime.getRuntime().availableProcessors();
    }
    static Path getConfigPath(){
        return Path.of(FabricLoader.getInstance().getConfigDir().toString(), "\\" + RheniumCore.MOD_ID) ;
    }

    static <T> List<T> fillList(List<T> t, int size){
        return fillList(t, size, null);
    }
    static <T> List<T> fillList(List<T> t, int size, T sample){
        t.clear();
        for(int i = 0; i < size; i++){
            t.add(sample);
        }
        return t;
    }
    static <T> T[] replaceArrayNull(T[] array, T sample){
        for(int i = 0; i < array.length; i++){
            if(array[i] == null){
                array[i] = sample;
            }
        }
        return array;
    }
}
