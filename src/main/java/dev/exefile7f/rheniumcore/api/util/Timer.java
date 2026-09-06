package dev.exefile7f.rheniumcore.api.util;

public class Timer{
    private long start;
    private long stop;
    public void start(){
        this.start = System.nanoTime();
    }
    public String stop(){
        stop = System.nanoTime();
        return (stop - start) + " ns";
    }
}
