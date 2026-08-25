package dev.exefile7f.rheniumcore.util;

import java.util.Map;

public class Entry<K, V> implements Map.Entry<K, V>{
    protected K key;
    protected V value;
    @Override
    public K getKey(){
        return key;
    }
    @Override
    public V getValue(){
        return value;
    }

    public Entry<K, V> setV(V value){
        this.value = value;
        return this;
    }
    public Entry<K, V> setK(K key){
        this.key =  key;
        return this;
    }
    @Override public V setValue(V value){return null;}

    @Override public boolean equals(Object o){return false;}
    @Override public int hashCode(){return 0;}
}
