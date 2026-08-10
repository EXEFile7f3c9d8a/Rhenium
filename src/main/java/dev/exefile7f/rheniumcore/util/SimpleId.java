package dev.exefile7f.rheniumcore.util;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleId{
    public AtomicInteger nextId = new AtomicInteger(0);
    public int nextId(){
        return this.nextId.getAndIncrement();
    }
    public int getId(){
        return this.nextId.get();
    }
}
