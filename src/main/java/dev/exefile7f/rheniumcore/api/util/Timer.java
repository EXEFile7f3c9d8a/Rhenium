package dev.exefile7f.rheniumcore.api.util;

public class Timer{
    protected long start;
    protected long stop;
    public void start(){
        this.start = System.nanoTime();
    }
    public String stopNS(){
        return stopNs() + " ns";
    }
    public long stopNs(){
        stop = System.nanoTime();
        return stop - start;
    }
    public String stopMS(){
        return stopMs() + "ms";
    }
    public double stopMs(){
        return stopNs() / 1_000_000.0;
    }
}
