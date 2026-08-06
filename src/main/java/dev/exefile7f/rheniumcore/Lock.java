package dev.exefile7f.rheniumcore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class Lock{
    public List<Thread> threads = new ArrayList<>();
    public void await(){
        synchronized(threads){
            threads.add(Thread.currentThread());
        }
        LockSupport.park();
    }
    public void signalAll(){
        List<Thread> t;
        synchronized(threads){
            t = new ArrayList<>(threads);
            threads.clear();
        }
        for(int i = 0; i < t.size(); i++){
            LockSupport.unpark(t.get(i));
        }
    }
}
