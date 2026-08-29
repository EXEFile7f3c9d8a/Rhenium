package dev.exefile7f.rheniumcore.statics;

import dev.exefile7f.rheniumcore.RheniumCore;
import dev.exefile7f.rheniumcore.util.threadpool.Tasks;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

import static dev.exefile7f.rheniumcore.util.Systems.getCores;

public interface StaticResource{
    int CPU_CORES = getCores();
    Path CONFIG_PATH = getConfigPath();

    //Tick Pool Status
    int STOP = - 1;
    int NO_TASK = 0;
    int HAVE_TASK = 1;

    static Path getConfigPath(){
        return Path.of(FabricLoader.getInstance().getConfigDir().toString(), "\\" + RheniumCore.MOD_ID + ".json") ;
    }
    static Tasks.Task[] replaceArrayNull(Tasks.Task[] array){
        for(int i = 0; i < array.length; i++){
            if(array[i] == null){
                array[i] = new Tasks.Task();
            }
        }
        return array;
    }
}
