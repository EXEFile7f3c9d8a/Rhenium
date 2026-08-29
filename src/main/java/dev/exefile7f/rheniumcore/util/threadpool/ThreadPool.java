package dev.exefile7f.rheniumcore.util.threadpool;

import dev.exefile7f.rheniumcore.util.Lock;
import dev.exefile7f.rheniumcore.util.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static dev.exefile7f.rheniumcore.statics.StaticResource.*;

public class ThreadPool{
    public static Id ID = new Id();
    public final int id;
    public byte status = NO_TASK;
    public List<TickThread> tickThreads = new ArrayList<>();
    public Tasks tasks = new Tasks();
    public Lock lock = new Lock();
    public ThreadPool(Map<String, Consumer<Tasks.Task>> computes){
        this.id = ID.nextId();
        for(int i = 0; i < CPU_CORES - 1; i++){
            tickThreads.add(new TickThread("TickThreadOf-" + this.id + "-" + i).setCompute(computes));
        }
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
            this.status = STOP;
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
