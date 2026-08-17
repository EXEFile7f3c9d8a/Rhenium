package dev.exefile7f.rheniumcore.util;

public final class Systems{
    private Systems(){}
    public static int getCores(){
        return Runtime.getRuntime().availableProcessors();
    }
}
