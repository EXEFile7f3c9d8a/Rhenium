package dev.exefile7f.rheniumcore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.exefile7f.rheniumcore.StaticResource.*;

public class ThreadPool{
    public static AtomicInteger nextId = new AtomicInteger(0);
    public final int id;
    public byte status = NO_TASK;
    public List<TickThread> tickThreads = new ArrayList<>();
    public Tasks tasks = new Tasks();
    public CountDownLatch startLatch = new CountDownLatch(1);
    public ThreadPool(){
        this.id = nextId.getAndIncrement();
        for(int i = 0; i < CPU_CORES - 1; i++){
            tickThreads.add(new TickThread("TickThreadOf-" + this.id + "-" + i));
        }
    }
    public void replaceTasks(Tasks tsk){
        this.tasks = tsk;
    }
    public void launch(){
        this.status = HAVE_TASK;
        this.startLatch.countDown();
    }
    public void stop(){
        this.status = NO_TASK;
    }
    public void kill(){
        this.status = STOP;
    }
    public void launchThreads(){
        for(int i = 0; i < tickThreads.size(); i++){
            TickThread tickThread = tickThreads.get(i);
            tickThread.thisThread = new Thread(() -> tickThread.accept(this), tickThread.id);
            tickThread.thisThread.start();
        }
    }
}
