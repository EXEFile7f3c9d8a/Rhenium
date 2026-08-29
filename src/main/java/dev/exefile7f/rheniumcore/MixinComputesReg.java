package dev.exefile7f.rheniumcore;

import dev.exefile7f.rheniumcore.util.threadpool.Tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MixinComputesReg{
    public MixinComputesReg(){}
    private Map<String, Consumer<Tasks.Task>> INPUT_COMPUTES = new HashMap<>();
    private Map<String, Consumer<Tasks.Task>> OUTPUT_COMPUTES = new HashMap<>();
    public void reg(Class<?> clazz, Consumer<Tasks.Task> input, Consumer<Tasks.Task> output){
        String name = clazz.getName();
        if(INPUT_COMPUTES.get(name) == null || OUTPUT_COMPUTES.get(name) == null){
            INPUT_COMPUTES.put(name, input);
            OUTPUT_COMPUTES.put(name, output);
        }
    }
    public Map<String, Consumer<Tasks.Task>> getInputMethods(){
        return INPUT_COMPUTES;
    }
    public Map<String, Consumer<Tasks.Task>> getOutputMethods(){
        return OUTPUT_COMPUTES;
    }
    public Consumer<Tasks.Task> getInput(String name){
        return INPUT_COMPUTES.get(name);
    }
    public Consumer<Tasks.Task> getOutput(String name){
        return OUTPUT_COMPUTES.get(name);
    }
}
