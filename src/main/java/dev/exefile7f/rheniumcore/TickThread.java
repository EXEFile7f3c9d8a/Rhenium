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
            int i = pool.tasks.located.getAndIncrement();
            if(i >= pool.tasks.tasks.size()){
                pool.pause();
                continue;
            }
            Tasks.Task currentTask = pool.tasks.tasks.get(i);
            COMPUTE_FUNCTIONS.get(currentTask.computeType).accept(currentTask);
        }
    }
}
