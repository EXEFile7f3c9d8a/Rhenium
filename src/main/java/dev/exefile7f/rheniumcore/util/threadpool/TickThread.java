package dev.exefile7f.rheniumcore.util.threadpool;

import dev.exefile7f.rheniumcore.RheniumCore;
import net.minecraft.world.tick.Tick;

import java.util.Map;
import java.util.function.Consumer;

import static dev.exefile7f.rheniumcore.statics.StaticResource.*;

public class TickThread implements Consumer<ThreadPool> {
    public final String id;
    public Thread thisThread;
    private Map<String, Consumer<Tasks.Task>> COMPUTE;
    public TickThread(String id){
        this.id = id;
    }
    public TickThread setCompute(Map<String, Consumer<Tasks.Task>> COMPUTE){
        this.COMPUTE = COMPUTE;
        return this;
    }
    @Override
    public void accept(ThreadPool pool){
        while(pool.status != STOP){
            if(pool.status == NO_TASK){
                pool.lock.await();
                continue;
            }
            pool.tasks.nextTask(this.COMPUTE, pool, pool.tasks.taskCounter);
        }
        RheniumCore.LOGGER.info("{} Shutting down!", this.id);
    }
}
