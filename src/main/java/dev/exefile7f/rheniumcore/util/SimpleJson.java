package dev.exefile7f.rheniumcore.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class SimpleJson{
    private Path file;
    private String indentation = "    ";
    private JsonValue box;

    public static class JsonValue{
        private String name;
        private Object value;
        private Object parent;
        public JsonValue get(String name){
            if(isObject()) return ((Map<String, JsonValue>) value).get(name);
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
        public Object getValue(){
            return value;
        }
        public String getName(){
            return name;
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
    public SimpleJson(){}
    public SimpleJson(Path file){
        setFile(file);
    }
    /**
     *
     */
    public void read() throws IOException{
        if(!isWritable()) throw new IOException("Not a writable file");
        enum Status{
            START,
            NONE,
            NAME,
            VALUE_STRING,
            VALUE_NUMBER,
            VALUE_MAP,
            VALUE_ARRAY,
            VALUE_BOOLEAN,
            VALUE_NULL_POSSIBLE,
            VALUE_NULL,
            VALUE_UNKNOW,

        }
        String file = Files.readString(this.file);
        Status status = Status.START;
        Deque<JsonValue> deque = new ArrayDeque<>();
        JsonValue json = new JsonValue();
        deque.push(json);
        for(int i = 0; i < file.length(); i++){
            char c = file.charAt(i);
            switch(status){
                case START, NONE -> {
                    switch(c){
                        case ' ', '\n' -> {
                        }
                        case '{' -> status = Status.VALUE_MAP;
                        case '"' -> status = Status.NAME;
                        default -> throw new IllegalArgumentException(
                                "Unexcepted character at " +
                                        Strings.toLineCharFormat(file, i) +
                                        " -> " +
                                        Strings.getNearCharacters(file, i, 10));
                    }
                }
            }
        }
    }
    public boolean isWritable(){
        return Files.isWritable(file);
    }
    public SimpleJson setFile(Path file){
        if(Files.isDirectory(file)) throw new IllegalArgumentException("Not a file: Path leads to a directory");
        else this.file = file;
        return this;
    }
    public SimpleJson setIndentation(String str){
        this.indentation = str;
        return this;
    }
    public SimpleJson setIndentation(int length){
        this.indentation = " ".repeat(length);
        return this;
    }
    public SimpleJson autoCreate(){
        try{
            Files.createFile(file);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        return this;
    }
}
