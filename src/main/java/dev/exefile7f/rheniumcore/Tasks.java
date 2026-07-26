package dev.exefile7f.rheniumcore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.exefile7f.rheniumcore.StaticResource.*;

public class Tasks{
    public static final class Task extends Tasks{
        public Object[] input;
        public int status;
        public int computeType;
        public Object[] output;
        public Task(){
            this.status = NONE;
            computeType = -1;
        }
    }
    public List<Task> tasks = new ArrayList<>();
    public AtomicInteger located = new AtomicInteger(0);
    public AtomicInteger size = new AtomicInteger(0);
}
