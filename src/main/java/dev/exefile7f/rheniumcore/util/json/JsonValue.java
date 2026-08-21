package dev.exefile7f.rheniumcore.util.json;

import dev.exefile7f.rheniumcore.util.Booleans;

import java.util.List;
import java.util.Map;

public class JsonValue{
    private String name;
    private Object value;
    private JsonValue parent;
    public JsonValue get(String name){
        if(isObject())return ((Map<String, JsonValue>) value).get(name);
        else return null;
    }
    public JsonValue setValue(Object value){
        this.value = value;
        return this;
    }
    public JsonValue setName(String name){
        this.name = name;
        return this;
    }
    public JsonValue setParent(JsonValue parent){
        this.parent = parent;
        return this;
    }
    public Object getValue(){
        return value;
    }
    public String getName(){
        return name;
    }
    public JsonValue getParent(){
        return parent;
    }
    public boolean isRoot(){
        return parent == null;
    }
    public boolean isObject(){
        return value instanceof Map;
    }

    public boolean isArray(){
        return value instanceof List;
    }

    public boolean isString(){
        return value instanceof String;
    }

    public boolean isNumber(){
        return value instanceof Number;
    }

    public boolean isBoolean(){
        return value instanceof Booleans;
    }

    public boolean isNull(){
        return value == null;
    }
}