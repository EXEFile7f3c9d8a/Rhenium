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
                try{
                    pool.startLatch.await();
                    continue;
                }catch(InterruptedException e){
                    throw new RuntimeException(e);
                }
            }
            if(pool.status == STOP){
                break;
            }
            int i = pool.tasks.located.getAndIncrement();
            if(i >= pool.tasks.tasks.size()){
                pool.stop();
                continue;
            }
            Tasks.Task currentTask = pool.tasks.tasks.get(i);
            COMPUTE_FUNCTIONS.get(currentTask.computeType).accept(currentTask);
        }
    }
}
