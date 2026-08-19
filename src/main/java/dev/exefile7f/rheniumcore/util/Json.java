package dev.exefile7f.rheniumcore.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static dev.exefile7f.rheniumcore.util.Jsons.isIllegalJsonNumber;

public class Json{
    public static class JsonValue{
        private String name;
        private Object value;
        private JsonValue parent;
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
            return value instanceof BigDecimal;
        }

        public boolean isBoolean(){
            return value instanceof Boolean;
        }

        public boolean isNull(){
            return value == null;
        }
    }
    private Path file;
    private String indentation = "    ";
    private JsonValue box;

    public Json(){}
    public Json(Path file){
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
            AFTER_NAME,
            AFTER_STATEMENT,
            VALUE_UNKNOW,
            VALUE_STRING,
            VALUE_NUMBER{

            },
            VALUE_MAP,
            VALUE_ARRAY,
            VALUE_BOOLEAN_TRUE,
            VALUE_BOOLEAN_FALSE,
            VALUE_NULL;
            int index = 1;
            void reset(){
                index = 1;
            }
        }
        BitMask tags = new BitMask();
        final int START = tags.create();
        final int CURRENT_IN_MAP = tags.create();
        final int CURRENT_IN_LIST = tags.create();
        final int CURRENT_NAMELESS = tags.create();
        final int AFTER_COMMA = tags.create();
        final int AFTER_BACKSLASH = tags.create();
        String file = Files.readString(this.file);
        Status status = Status.START;
        Deque<JsonValue> deque = new ArrayDeque<>();
        StringBuilder sb = new StringBuilder();
        JsonValue root = new JsonValue().setParent(null);
        deque.push(root);
        tags.enable(START).enable(CURRENT_NAMELESS);
        for(int i = 0; i < file.length(); i++){
            char c = file.charAt(i);
            switch(status){
                case START -> {
                    switch(c){
                        case ' ', '\n' -> {}
                        case '{' -> status = Status.VALUE_MAP;
                        case '"' -> status = Status.NAME;
                        default -> throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                    }
                }
                case NONE -> {

                }
                case AFTER_STATEMENT -> {
                    switch(c){
                        case ' ', '\n' -> {}
                        case ',' -> tags.enable(AFTER_COMMA);
                        case ']' -> {
                            if(tags.isSet(AFTER_COMMA) || !deque.element().isArray())throw new IllegalArgumentException(Exceptions.unexpectedClosing(c, file, i));
                            else{
                                if(deque.pop().getParent().isArray()){
                                    status = Status.VALUE_ARRAY;
                                }else{
                                    status = Status.VALUE_MAP;
                                    tags.disable(CURRENT_NAMELESS);
                                }
                            }
                        }
                        case '}' -> {

                        }
                    }
                }
                case NAME -> {
                    switch(c){
                        case '"' -> {
                            deque.element().setName(sb.toString());
                            sb.setLength(0);
                            status = Status.AFTER_NAME;
                        }
                        case '\n', '\r' -> throw new IllegalArgumentException(Exceptions.unexpectedLineBreak(file, i));
                        default -> sb.append(c);
                    }
                }
                case AFTER_NAME -> {
                    switch(c){
                        case ' ', '\n' -> {}
                        case ':' -> status = Status.VALUE_UNKNOW;
                        default -> throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                    }
                }
                case VALUE_UNKNOW -> {
                    switch(c){
                        case ' ', '\n' -> {}
                        case '"' -> status = Status.VALUE_STRING;
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' -> {
                            sb.append(c);
                            status = Status.VALUE_NUMBER;
                        }
                        case '{' -> {
                            deque.element().setValue(new HashMap<String, JsonValue>());
                            status = Status.VALUE_MAP;
                        }
                        case '[' -> {
                            deque.element().setValue(new ArrayList<JsonValue>());
                            status = Status.VALUE_ARRAY;
                        }
                        case 't' -> status = Status.VALUE_BOOLEAN_TRUE;
                        case 'f' -> status = Status.VALUE_BOOLEAN_FALSE;
                        case 'n' -> status = Status.VALUE_NULL;
                        default ->  throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                    }
                }
                case VALUE_STRING -> {
                    switch(c){
                        case '"' -> {
                            deque.element().setValue(sb.toString());
                            sb.setLength(0);
                            status = Status.AFTER_STATEMENT;
                        }
                        case '\n' -> throw new IllegalArgumentException(Exceptions.unexpectedLineBreak(file, i));
                        default -> sb.append(c);
                    }
                }
                case VALUE_NUMBER -> {
                    switch(c){
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', 'e', 'E', '.' -> sb.append(c);
                        case ' ', ',' -> {
                            if(isIllegalJsonNumber(sb.toString()))throw new IllegalArgumentException(Exceptions.invalidJsonNumber(file, i, sb.toString()));
                            else{
                                deque.element().setValue(new BigDecimal(sb.toString()));
                                sb.setLength(0);
                                status = Status.AFTER_STATEMENT;
                            }
                        }
                        case '\n' -> throw new IllegalArgumentException(Exceptions.unexpectedLineBreak(file, i));
                        default ->  throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                    }
                }
                case VALUE_MAP -> {
                    switch(c){
                        case ' ', '\n' -> {}
                        case '}' -> {

                        }
                    }
                }
            }
        }
    }
    private static final class Exceptions{
        private Exceptions(){}
        public static String unexpectedChar(char c, String file, int i){
            return "Unexpected character '" + c + "' at " +
                    Strings.toLineCharFormat(file, i) +
                    " (index " + i + ")";
        }
        public static String unexpectedLineBreak(String file, int i){
            return "Unexpected line break at " +
                    Strings.toLineCharFormat(file, i) +
                    " (index " + i + ")";
        }
        public static String invalidJsonNumber(String file, int i, String number){
            i -= number.length();
            return "Invalid JSON number at " +
                    Strings.toLineCharFormat(file, i) +
                    " (index " + i + ") -> " +
                    number;
        }
        public static String unexpectedClosing(char c, String file, int i){
            return "Unexpected closing '" + c + "' at " +
                    Strings.toLineCharFormat(file, i) +
                    " (index " + i + ")";
        }
    }
    public boolean isWritable(){
        return Files.isWritable(file);
    }
    public Json setFile(Path file){
        if(Files.isDirectory(file)) throw new IllegalArgumentException("Not a file: Path leads to a directory");
        else this.file = file;
        return this;
    }
    public Json setIndentation(String str){
        this.indentation = str;
        return this;
    }
    public Json setIndentation(int length){
        this.indentation = " ".repeat(length);
        return this;
    }
    public Json autoCreate(){
        try{
            Files.createFile(file);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        return this;
    }

}
