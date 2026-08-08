package dev.exefile7f.rheniumcore;

import java.util.function.Consumer;

import static dev.exefile7f.rheniumcore.StaticResource.*;

public class TickThread implements Consumer<ThreadPool> {
    public final String id;
    public Thread thisThread;
    public TickThread(String id){
        this.id = id;
    }
    @Override
    public void accept(ThreadPool pool){
        while(pool.status != STOP){
            if(pool.status == NO_TASK){
                pool.lock.await();
                continue;
            }
            pool.tasks.nextTask(COMPUTE_FUNCTIONS, pool, pool.tasks.taskCounter);
        }
        RheniumCore.LOGGER.info("{} Shutting down!", this.id);
    }
}
