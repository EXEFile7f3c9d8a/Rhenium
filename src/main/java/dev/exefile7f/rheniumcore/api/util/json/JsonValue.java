package dev.exefile7f.rheniumcore.api.util.json;

import dev.exefile7f.rheniumcore.api.util.ArrayMap;
import dev.exefile7f.rheniumcore.api.util.Entry;
import dev.exefile7f.rheniumcore.api.util.RawNumber;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

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
    @Nullable @Contract(pure = true)
    public JsonValue get(int index){
        if(this.isArray())return ((List<JsonValue>)value).get(--index);
        else return null;
    }
    @Nullable @Contract(pure = true)
    public JsonValue get(String name){
        if(this.isObject())return this.getAsObject().get(name);
        else return null;
    }
    public JsonValue(){}
    @Override @Nullable @Contract(pure = true)
    public String toString(){
        return this.toString(0, "", true);
    }
    @Nullable @Contract(pure = true)
    public String toStringFormatted(){
        return this.toString(0, "    ", false);
    }
    @Nullable @Contract(pure = true)
    public String toString(String indentation){
        return this.toString(0, indentation, false);
    }
    @Nullable @Contract(pure = true)
    protected String toString(int depth, boolean compact){
        return this.toString(depth, compact ? "" : "    ", compact);
    }
    @Nullable @Contract(pure = true)
    protected String toString(int depth, String indentation, boolean compact){
        return this.toString(depth, indentation, new StringBuilder(), compact).toString();
    }
    @Nullable @Contract(pure = true)
    protected StringBuilder toString(int depth, String indentation, StringBuilder sb, boolean compact){
        if(this.isObject()){
            depth++;
            sb.append('{');
            var linebreak = compact ? "" : '\n';
            ArrayMap<String, JsonValue> map = this.getAsObject();
            List<Entry<String, JsonValue>> entries = new ArrayList<>(map.getEntries());
            for(int i = 0; i < map.size(); i++){
                Entry<String, JsonValue> entry = entries.get(i);
                sb.append(linebreak)
                  .repeat(indentation, depth)
                  .append('"')
                  .append(entry.getKey())
                  .append("\":")
                  .append(compact ? "" : ' ');
                entry.getValue().toString(depth, indentation, sb, compact);
                sb.append(',');
            }
            sb.setLength(sb.length() - 1);
            sb.append(linebreak).repeat(indentation, --depth).append('}');
        }else if(this.isArray()){
            depth++;
            sb.append('[');
            var linebreak = compact ? "" : '\n';
            List<JsonValue> list = this.getAsArray();
            for(int i = 0; i < list.size(); i++){
                sb.append(linebreak).repeat(indentation, depth);
                list.get(i).toString(depth, indentation, sb, compact);
                sb.append(',');
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
    @Nullable @Contract(pure = true)
    public ArrayMap<String, JsonValue> getAsObject(){
        if(this.isObject())return (ArrayMap<String, JsonValue>)getValue();
        else return null;
    }
    @Nullable @Contract(pure = true)
    public List<JsonValue> getAsArray(){
        if(this.isArray())return (List<JsonValue>)getValue();
        else return null;
    }
    @Nullable @Contract(pure = true)
    public String getAsString(){
        if(this.isString())return (String)getValue();
        else return null;
    }
    @Nullable @Contract(pure = true)
    public Object getValue(){
        return value;
    }
    @Nullable @Contract(pure = true)
    public String getName(){
        return name;
    }
    @Nullable @Contract(pure = true)
    public JsonValue getParent(){
        return parent;
    }

    @Contract(pure = true)
    public boolean isRoot(){
        return parent == null;
    }
    @Contract(pure = true)
    public boolean isObject(){
        return value instanceof Map;
    }
    @Contract(pure = true)
    public boolean isArray(){
        return value instanceof List;
    }
    @Contract(pure = true)
    public boolean isString(){
        return value instanceof String;
    }
    @Contract(pure = true)
    public boolean isNumber(){
        return value instanceof Number;
    }
    @Contract(pure = true)
    public boolean isBoolean(){
        return value instanceof Boolean;
    }
    @Contract(pure = true)
    public boolean isNull(){
        return value == null;
    }
}