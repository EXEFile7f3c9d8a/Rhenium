package dev.exefile7f.rheniumcore.api.util.threadpool;

import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

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
    protected ThreadPool parentPool;
    protected Task[] tasks = new Task[512];
    protected AtomicInteger taskCounter = new AtomicInteger(0);
    protected AtomicInteger writeCounter = new AtomicInteger(0);
    protected AtomicInteger size = new AtomicInteger(0);
    public Tasks(@NonNull ThreadPool pool){
        this.parentPool = pool;
    }
    public void reset(){
        synchronized(tasks){
            Arrays.fill(tasks, null);
            taskCounter.set(0);
            size.set(0);
        }
    }
    public AtomicInteger getWriteCounter(){
        return this.writeCounter;
    }
    public AtomicInteger getTaskCounter(){
        return this.taskCounter;
    }
    public boolean isDone(int i){
        return i >= this.size.get();
    }
    public Task getEmptyTask(){
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
                tasks = Arrays.copyOf(tasks, (tasks.length) * 2);
                for(int i = 0; i < tasks.length; i++){
                    if(tasks[i] == null)tasks[i] = new Task();
                }
            }
            size.set(size.get() * 2);
        }
    }
    public void nextTask(@NonNull Map<String, Consumer<Task>> COMPUTE_FUNCTIONS, @NonNull AtomicInteger counter){
        synchronized(tasks){
            int i = counter.getAndIncrement();
            if(isDone(i)){
                if(parentPool != null)parentPool.pause();
                return;
            }
            Task current = tasks[i];
            COMPUTE_FUNCTIONS.get(current.computeType.toString()).accept(current);
        }
    }
    public void taskAll(@NonNull Map<String, Consumer<Task>> COMPUTE_FUNCTIONS, @NonNull AtomicInteger counter){
        synchronized(taskCounter){
            while(!isDone(this.taskCounter.get())){
                nextTask(COMPUTE_FUNCTIONS, counter);
            }
        }
    }
}
