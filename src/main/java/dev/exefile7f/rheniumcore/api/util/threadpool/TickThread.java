package dev.exefile7f.rheniumcore.api.util.threadpool;

import dev.exefile7f.rheniumcore.RheniumCore;

import java.util.Map;
import java.util.function.Consumer;

import static dev.exefile7f.rheniumcore.api.util.threadpool.ThreadPool.ThreadPoolStatus.NO_TASK;
import static dev.exefile7f.rheniumcore.api.util.threadpool.ThreadPool.ThreadPoolStatus.STOP;

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
            pool.tasks.nextTask(this.COMPUTE, pool.tasks.taskCounter);
        }
        RheniumCore.LOGGER.info("{}-thread shutting down!", this.id);
    }
}
