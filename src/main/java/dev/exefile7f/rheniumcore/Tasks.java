package dev.exefile7f.rheniumcore;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static dev.exefile7f.rheniumcore.StaticResource.*;

public class Tasks{
    public static class Task{
        public Object[] input;
        public int status;
        public int computeType;
        public Object[] output;
        public Task(){
            this.status = NONE;
            computeType = -1;
        }
        public Task putInput(Object obj){
            synchronized(input){
                input[input.length - 1] = obj;
            }
            return this;
        }
        public Task setStatus(int i){
            synchronized(input){
                this.status = i;
            }
            return this;
        }
        public Task setComputeType(int i){
            synchronized(input){
                this.computeType = i;
            }
            return this;
        }
        public Task addOutput(Object obj){
            synchronized(input){
                this.output[output.length - 1] = obj;
            }
            return this;
        }
        public Task setInput(Object obj, int index){
            synchronized(input){
                this.input[index] = obj;
            }
            return this;
        }
        public Task setOutput(Object obj, int index){
            synchronized(output){
                this.output[index] = obj;
            }
            return this;
        }
    }
    public Task[] tasks = new Task[512];
    public AtomicInteger located = new AtomicInteger(0);
    public AtomicInteger size = new AtomicInteger(0);
    public void reset(){
        synchronized(tasks){
            Arrays.fill(tasks, null);
            located.set(0);
            size.set(0);
        }
    }
    public boolean isDone(){
        return isDone(located.get());
    }
    public boolean isDone(int i){
        return i >= this.size.get();
    }
    public Task getNearestEmptyTask(){
        return tasks[size.get()];
    }
    public Tasks addTask(Task tsk){
        synchronized(tasks){
            checkSizeLimit();
            tasks[size.getAndIncrement()] = tsk;
        }
        return this;
    }
    public void checkSizeLimit(){
        if(size.get() >= tasks.length){
            tasks = replaceArrayNull(Arrays.copyOf(tasks, (tasks.length) * 2), new Task());
        }
    }
    public void nextTask(List<Consumer<Task>> COMPUTE_FUNCTIONS){
        nextTask(COMPUTE_FUNCTIONS, null);
    }
    public void nextTask(List<Consumer<Task>> COMPUTE_FUNCTIONS, ThreadPool pool){
        synchronized(tasks){
            int i = this.located.getAndIncrement();
            if(isDone()){
                if(pool != null)pool.pause();
                return;
            }
            Task current = tasks[i];
            COMPUTE_FUNCTIONS.get(current.computeType).accept(current);
        }
    }
    public void runAllTask(List<Consumer<Task>> COMPUTE_FUNCTIONS, ThreadPool pool){
        int t = this.located.get();
        while(isDone()){
            nextTask(COMPUTE_FUNCTIONS, pool);
        }
    }
    public void nextWrite(List<Consumer<Task>> WRITE_FUNCTIONS, ThreadPool pool){

    }
    public void writeAll(List<Consumer<Task>> WRITE_FUNCTIONS, ThreadPool pool){

    }
}
