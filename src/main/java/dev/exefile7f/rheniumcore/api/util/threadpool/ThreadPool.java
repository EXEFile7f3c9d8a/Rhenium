package dev.exefile7f.rheniumcore.api.util.threadpool;

import dev.exefile7f.rheniumcore.api.util.Lock;
import dev.exefile7f.rheniumcore.api.util.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static dev.exefile7f.rheniumcore.api.util.Systems.getCores;
import static dev.exefile7f.rheniumcore.api.util.threadpool.ThreadPool.ThreadPoolStatus.HAVE_TASK;
import static dev.exefile7f.rheniumcore.api.util.threadpool.ThreadPool.ThreadPoolStatus.NO_TASK;

public class ThreadPool{
    public static Id ID = new Id();
    public final int id;
    public ThreadPoolStatus status = NO_TASK;
    public List<TickThread> tickThreads = new ArrayList<>();
    public Tasks tasks = new Tasks(this);
    public Lock lock = new Lock();
    public ThreadPool(Map<String, Consumer<Tasks.Task>> computes){
        this.id = ID.nextId();
        for(int i = 0; i < getCores() - 1; i++){
            tickThreads.add(new TickThread("TickThreadOf-" + this.id + "-" + i).setCompute(computes));
        }
    }
    public enum ThreadPoolStatus{
        STOP,
        NO_TASK,
        HAVE_TASK
    }
    public void replaceTasks(Tasks tsk){
        synchronized(lock){
            this.tasks = tsk;
        }
    }
    public void launch(){
        synchronized(lock){
            this.status = HAVE_TASK;
            this.lock.signalAll();
        }
    }
    public void pause(){
        synchronized(lock){
            this.status = NO_TASK;
            tasks.notify();
        }
    }
    public void kill(){
        synchronized(lock){
            this.status = ThreadPoolStatus.STOP;
            this.lock.signalAll();
        }
    }
    public void launchThreads(){
        synchronized(tickThreads){
            for(int i = 0; i < tickThreads.size(); i++){
                TickThread tickThread = tickThreads.get(i);
                tickThread.thisThread = new Thread(() -> tickThread.accept(this), tickThread.id);
                tickThread.thisThread.start();
            }
        }
    }
}
