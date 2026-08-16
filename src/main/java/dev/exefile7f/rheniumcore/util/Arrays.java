package dev.exefile7f.rheniumcore.util;

import java.util.List;

public class Arrays{
    public static <T> List<T> fillList(List<T> t, int size){
        return fillList(t, size, null);
    }
    public static <T> List<T> fillList(List<T> t, int size, T sample){
        t.clear();
        for(int i = 0; i < size; i++){
            t.add(sample);
        }
        return t;
    }
}
