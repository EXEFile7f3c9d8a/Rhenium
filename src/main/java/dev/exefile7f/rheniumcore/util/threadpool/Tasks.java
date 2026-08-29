package dev.exefile7f.rheniumcore.util.threadpool;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static dev.exefile7f.rheniumcore.statics.StaticResource.*;

public class Tasks{
    public static class Task{
        public Object[] input;
        public Class<?> computeType;
        public Object[] output;
        public Task(){
            computeType = Void.class;
        }
        public Task putInputs(Object obj){
            synchronized(input){
                input[input.length - 1] = obj;
            }
            return this;
        }
        public Task putInputs(Object... objs){
            for(int i = 0; i < objs.length; i++){
                this.putInputs(objs[i]);
            }
            return this;
        }
        public Task setComputeType(Class<?> clazz){
            synchronized(input){
                this.computeType = clazz;
            }
            return this;
        }
        public Task putOutputs(Object... objs){
            for(int i = 0; i < objs.length; i++){
                this.putOutput(objs[i]);
            }
            return this;
        }
        public Task putOutput(Object obj){
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
    public AtomicInteger taskCounter = new AtomicInteger(0);
    public AtomicInteger writeCounter = new AtomicInteger(0);
    public AtomicInteger size = new AtomicInteger(0);
    public void reset(){
        synchronized(tasks){
            Arrays.fill(tasks, null);
            taskCounter.set(0);
            size.set(0);
        }
    }
    public boolean isDone(int i){
        return i >= this.size.get();
    }
    public Task getNearestEmptyTask(){
        return tasks[size.get()];
    }
    public Tasks addTask(Task tsk){
        synchronized(tasks){
            grow();
            tasks[size.getAndIncrement()] = tsk;
        }
        return this;
    }
    public void grow(){
        synchronized(tasks){
            if(size.get() >= tasks.length){
                tasks = replaceArrayNull(Arrays.copyOf(tasks, (tasks.length) * 2));
                for(int i = 0; i < tasks.length; i++){
                    if(tasks[i] == null)tasks[i] = new Task();
                }
            }
        }
    }
    public void nextTask(Map<String, Consumer<Task>> COMPUTE_FUNCTIONS, ThreadPool pool, AtomicInteger counter){
        synchronized(tasks){
            int i = counter.getAndIncrement();
            if(isDone(i)){
                if(pool != null)pool.pause();
                return;
            }
            Task current = tasks[i];
            COMPUTE_FUNCTIONS.get(current.computeType.toString()).accept(current);
        }
    }
    public void taskAll(Map<String, Consumer<Task>> COMPUTE_FUNCTIONS, ThreadPool pool, AtomicInteger counter){
        synchronized(taskCounter){
            while(!isDone(this.taskCounter.get())){
                nextTask(COMPUTE_FUNCTIONS, pool, counter);
            }
        }
    }
}
