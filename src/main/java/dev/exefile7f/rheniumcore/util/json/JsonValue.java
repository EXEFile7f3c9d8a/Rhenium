package dev.exefile7f.rheniumcore.util.json;

import dev.exefile7f.rheniumcore.util.ArrayMap;
import dev.exefile7f.rheniumcore.util.Entry;
import dev.exefile7f.rheniumcore.util.RawNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonValue{
    private String name;
    private Object value;
    private JsonValue parent;
    public JsonValue get(int index){
        if(this.isArray())return ((List<JsonValue>)value).get(--index);
        else return null;
    }
    public JsonValue get(String name){
        if(this.isObject())return this.getAsObject().get(name);
        else return null;
    }
    public JsonValue(){}
    @Override
    public String toString(){
        return toString(0, "    ");
    }
    protected String toString(String indentation){
        return toString(0, indentation);
    }
    protected String toString(int depth){
        return toString(depth, "    ");
    }
    private String toString(int depth, String indentation){
        StringBuilder sb = new StringBuilder();
        if(this.isObject()){
            depth++;
            sb.append('{');
            ArrayMap<String, JsonValue> map = this.getAsObject();
            List<Entry<String, JsonValue>> entries = new ArrayList<>(map.getEntries());
            for(int i = 0; i < map.size(); i++){
                Entry<String, JsonValue> entry = entries.get(i);
                sb.append('\n')
                  .repeat(indentation, depth)
                  .append('"')
                  .append(entry.getKey())
                  .append("\": ")
                  .append(entry.getValue().toString(depth))
                  .append(',');
            }
            sb.setLength(sb.length() - 1);
            sb.append("\n").repeat(indentation, --depth).append('}');
        }else if(this.isArray()){
            depth++;
            sb.append('[');
            List<JsonValue> list = (List<JsonValue>)value;
            for(int i = 0; i < list.size(); i++){
                sb.append('\n')
                  .repeat(indentation, depth)
                  .append(list.get(i).toString(depth))
                  .append(',');
            }
            sb.setLength(sb.length() - 1);
            sb.append("\n").repeat(indentation, --depth).append(']');
        }else if(this.isNumber())sb.append(((RawNumber)value).getOriginal());
        else if(this.isString())sb.append('"').append((String)value).append('"');
        else if(this.isBoolean() || this.isNull())sb.append(value);
        return sb.toString();
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
    public ArrayMap<String, JsonValue> getAsObject(){
        if(this.isObject())return (ArrayMap<String, JsonValue>)getValue();
        else return null;
    }
    public List<JsonValue> getAsArray(){
        if(this.isObject())return (List<JsonValue>)getValue();
        else return null;
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
        return value instanceof Boolean;
    }
    public boolean isNull(){
        return value == null;
    }
}