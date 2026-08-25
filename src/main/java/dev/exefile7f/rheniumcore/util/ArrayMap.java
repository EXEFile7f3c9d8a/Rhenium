package dev.exefile7f.rheniumcore.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArrayMap<K, V> extends HashMap<K, V>{
    private final List<K> list = new ArrayList<>();
    public ArrayMap(){
        super();
    }
    public List<dev.exefile7f.rheniumcore.util.Entry<K, V>> getEntries(){
        List<dev.exefile7f.rheniumcore.util.Entry<K, V>> t = new ArrayList<>();
        for(int i = 0; i < super.size(); i++)t.add(new dev.exefile7f.rheniumcore.util.Entry<K, V>().setK(list.get(i)).setV(super.get(list.get(i))));
        return t;
    }
    @Override
    public V put(K key, V value){
        list.add(key);
        return super.put(key, value);
    }
}
