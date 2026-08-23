package dev.exefile7f.rheniumcore.util.json;

import dev.exefile7f.rheniumcore.util.BitMask;
import dev.exefile7f.rheniumcore.util.Strings;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

import static dev.exefile7f.rheniumcore.util.json.Jsons.isIllegalJsonNumber;

public class Json{
    private Path file;
    private String indentation = "    ";
    private JsonValue box;

    public Json(){}
    public Json(Path file){
        setFile(file);
    }
    protected enum Status{
        NONE,
        NAME,
        AFTER_NAME,
        AFTER_STATEMENT,
        VALUE_UNKNOW,
        VALUE_STRING,
        VALUE_NUMBER,
        VALUE_MAP,
        VALUE_ARRAY,
        VALUE_BOOLEAN_TRUE,
        VALUE_BOOLEAN_FALSE,
        VALUE_NULL
    }
    /**
     *
     */
    public void read() throws IOException{
        if(!isWritable()) throw new IOException("Not a writable file");
        BitMask tags = new BitMask();
        final int START = tags.create();
        final int CURRENT_NAMELESS = tags.create();
        final int AFTER_COMMA = tags.create();
        final int AFTER_BACKSLASH = tags.create();
        String file = Files.readString(this.file);
        Status status = Status.VALUE_UNKNOW;
        Deque<JsonValue> deque = new ArrayDeque<>();
        StringBuilder sb = new StringBuilder();
        JsonValue root = new JsonValue().setParent(null);
        deque.push(root);
        tags.enable(START).enable(CURRENT_NAMELESS);
        for(int i = 0; i < file.length(); i++){
            char c = file.charAt(i);
            switch(status){
                case NONE -> {
                    switch(c){
                        case ' ', '\n' -> {}
                        case '"' -> {
                            tags.disable(AFTER_COMMA);
                            status = Status.NAME;
                        }
                        case ']' -> ParserFunction.closing(tags, AFTER_COMMA, deque, c, file, i, CURRENT_NAMELESS, status, false);
                        case '}' -> ParserFunction.closing(tags, AFTER_COMMA, deque, c, file, i, CURRENT_NAMELESS, status, true);
                        default ->  throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                    }
                }
                case AFTER_STATEMENT -> {
                    switch(c){
                        case ' ', '\n' -> {}
                        case ',' -> {
                            if(tags.isSet(AFTER_COMMA))throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                            else{
                                if(deque.element().isObject())status = Status.NONE;
                                else status = Status.VALUE_UNKNOW;
                                tags.enable(AFTER_COMMA);
                            }
                        }
                        case ']' -> ParserFunction.closing(tags, AFTER_COMMA, deque, c, file, i, CURRENT_NAMELESS, status, false);
                        case '}' -> ParserFunction.closing(tags, AFTER_COMMA, deque, c, file, i, CURRENT_NAMELESS, status, true);
                        default ->  throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                    }
                }
                case NAME -> {
                    switch(c){
                        case '"' -> {
                            if(tags.isSet(AFTER_BACKSLASH)){
                                tags.disable(AFTER_BACKSLASH);
                                sb.append(c);
                            }else{
                                if(deque.element().isRoot())deque.push(new JsonValue()
                                        .setName(sb.toString())
                                        .setParent(deque.element()));
                                sb.setLength(0);
                                status = Status.AFTER_NAME;
                            }
                        }
                        case '\\' -> {
                            tags.flip(AFTER_BACKSLASH);
                            sb.append(c);
                        }
                        case '\n', '\r' -> throw new IllegalArgumentException(Exceptions.unexpectedLineBreak(file, i));
                        default -> {
                            tags.disable(AFTER_BACKSLASH);
                            sb.append(c);
                        }
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
                        case '"' -> {
                            ParserFunction.VALUE_UNKNOW_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    JsonValue.Type.STRING,
                                    null
                            );
                            status = Status.VALUE_STRING;
                        }
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' -> {
                            ParserFunction.VALUE_UNKNOW_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    JsonValue.Type.NUMBER,
                                    null
                            );
                            sb.append(c);
                            status = Status.VALUE_NUMBER;
                        }
                        case '{' -> {
                            ParserFunction.VALUE_UNKNOW_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    new HashMap<String, JsonValue>(),
                                    JsonValue.Type.OBJECT,
                                    null
                            );
                            status = Status.VALUE_MAP;
                        }
                        case '[' -> {
                            ParserFunction.VALUE_UNKNOW_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    new ArrayList<JsonValue>(),
                                    JsonValue.Type.ARRAY,
                                    null
                            );

                            status = Status.VALUE_ARRAY;
                        }
                        case 't', 'f' -> {
                            ParserFunction.VALUE_UNKNOW_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    JsonValue.Type.BOOLEAN,
                                    null
                            );
                            if(c == 't')status = Status.VALUE_BOOLEAN_TRUE;
                            else status = Status.VALUE_BOOLEAN_FALSE;

                        }
                        case 'n' -> {
                            ParserFunction.VALUE_UNKNOW_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    JsonValue.Type.NULL,
                                    null
                            );
                            status = Status.VALUE_NULL;
                        }
                        default ->  throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                    }
                }
                case VALUE_STRING -> {
                    switch(c){
                        case '"' -> {
                            deque.element().setValue(sb.toString());
                            deque.pop();
                            sb.setLength(0);
                            status = Status.AFTER_STATEMENT;
                        }
                        case '\\' -> {
                            tags.flip(AFTER_BACKSLASH);
                            sb.append(c);
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
                                deque.pop();
                                sb.setLength(0);
                                status = Status.AFTER_STATEMENT;
                                i--;
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
    private static final class ParserFunction{
        private ParserFunction(){}
        public static void VALUE_UNKNOW_ifNamelessPush(
                BitMask tags,
                int START,
                int CURRENT_NAMELESS,
                Deque<JsonValue> deque,
                JsonValue.Type type,
                String name
        ){
            if(tags.isSet(CURRENT_NAMELESS))deque.push(new JsonValue()
                    .setType(type)
                    .setParent(tags.isSet(START) ? null : deque.element())
                    .setName(name));
        }
        public static void VALUE_UNKNOW_ifNamelessPush(
                BitMask tags,
                int START,
                int CURRENT_NAMELESS,
                Deque<JsonValue> deque,
                Object value,
                JsonValue.Type type,
                String name
        ){
            if(tags.isSet(CURRENT_NAMELESS))deque.push(new JsonValue()
                    .setValue(value)
                    .setType(type)
                    .setParent(tags.isSet(START) ? null : deque.element())
                    .setName(name));
            else deque.element().setValue(value);
        }

        public static void closing(
                BitMask tags,
                int AFTER_COMMA,
                Deque<JsonValue> deque,
                char c,
                String file,
                int i,
                int CURRENT_NAMELESS,
                Status status,
                boolean isObject
        ){
            if(tags.isSet(AFTER_COMMA) || isObject ? !deque.element().isObject() : !deque.element().isArray())
                throw new IllegalArgumentException(Exceptions.unexpectedClosing(c, file, i));
            else {
                if(deque.pop().getParent().isArray()){
                    tags.enable(CURRENT_NAMELESS);
                    status = Status.VALUE_UNKNOW;
                }else{
                    tags.disable(CURRENT_NAMELESS);
                    status = Status.NONE;
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
