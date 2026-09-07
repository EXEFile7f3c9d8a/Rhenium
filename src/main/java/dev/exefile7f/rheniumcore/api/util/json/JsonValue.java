package dev.exefile7f.rheniumcore.api.util.json;

import dev.exefile7f.rheniumcore.api.util.ArrayMap;
import dev.exefile7f.rheniumcore.api.util.Entry;
import dev.exefile7f.rheniumcore.api.util.RawNumber;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A in-memory format of Json data
 *
 * <p>This class saves Json data as a small object in a tree structure, for example;
 * {@snippet lang=JSON:
 *         {
 *             "number": -123E+123,
 *             "string": "string",
 *             "boolean": true,
 *             "null": null
 *         }
 * } the root {} will be a {@link Map}/{@link HashMap} inside of {@link JsonValue}
 * that have 4 values in it.</p>
 *
 */
public class JsonValue{
    protected String name;
    protected Object value;
    protected JsonValue parent;
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
        return this.toString(0, "", "");
    }
    public String toStringFormatted(){
        return this.toString(0, "    ", "\n");
    }
    public String toString(String indentation){
        return this.toString(0, indentation, "\n");
    }
    protected String toString(int depth, String linebreak){
        return this.toString(depth, "    ", linebreak);
    }
    protected String toString(int depth, String indentation, String linebreak){
        return this.toString(depth, indentation, new StringBuilder(), linebreak).toString();
    }
    protected StringBuilder toString(int depth, String indentation, StringBuilder sb, String linebreak){
        if(this.isObject()){
            depth++;
            sb.append('{');
            ArrayMap<String, JsonValue> map = this.getAsObject();
            List<Entry<String, JsonValue>> entries = new ArrayList<>(map.getEntries());
            for(int i = 0; i < map.size(); i++){
                Entry<String, JsonValue> entry = entries.get(i);
                sb.append(linebreak)
                  .repeat(indentation, depth)
                  .append('"')
                  .append(entry.getKey())
                  .append("\": ")
                  .append(entry.getValue().toString(depth, indentation, linebreak))
                  .append(',');
            }
            sb.setLength(sb.length() - 1);
            sb.append(linebreak).repeat(indentation, --depth).append('}');
        }else if(this.isArray()){
            depth++;
            sb.append('[');
            List<JsonValue> list = (List<JsonValue>)value;
            for(int i = 0; i < list.size(); i++){
                sb.append(linebreak)
                  .repeat(indentation, depth)
                  .append(list.get(i).toString(depth, indentation, linebreak))
                  .append(',');
            }
            sb.setLength(sb.length() - 1);
            sb.append(linebreak).repeat(indentation, --depth).append(']');
        }else if(this.isNumber())sb.append(((RawNumber)value).getOriginal());
        else if(this.isString())sb.append('"').append(getAsString()).append('"');
        else if(this.isBoolean() || this.isNull())sb.append(value);
        return sb;
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
        if(this.isArray())return (List<JsonValue>)getValue();
        else return null;
    }
    public String getAsString(){
        if(this.isString())return (String)getValue();
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