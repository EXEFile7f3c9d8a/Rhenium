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
        VALUE_UNKNOWN,
        VALUE_STRING,
        VALUE_NUMBER,
        VALUE_BOOLEAN_TRUE("true", true),
        VALUE_BOOLEAN_FALSE("false", false),
        VALUE_NULL("null", null);

        String sample;
        Object value;
        Status(){}
        Status(String sample,Object value){
            this.sample=sample;
            this.value=value;
        }
    }
    /**
     *
     */
    public void read() throws IOException{
        if(!isReadable())throw new IOException("Not a readable file");
        String file = Files.readString(this.file);
        if(file.isEmpty()){
            this.box = null;
            return;
        }
        BitMask tags = new BitMask();
        final int START = tags.create();
        final int CURRENT_NAMELESS = tags.create();
        final int AFTER_COMMA = tags.create();
        final int AFTER_BACKSLASH = tags.create();
        Status status = Status.VALUE_UNKNOWN;
        Deque<JsonValue> deque = new ArrayDeque<>();
        StringBuilder sb = new StringBuilder();
        JsonValue root = new JsonValue().setParent(null);
        deque.push(root);
        tags.enable(START).enable(CURRENT_NAMELESS);
        int i = 0;
        for(; i < file.length(); i++){
            char c = file.charAt(i);
            switch(status){
                case NONE -> {
                    switch(c){
                        case ' ', '\n' -> {}
                        case '"' -> {
                            tags.disable(AFTER_COMMA);
                            status = Status.NAME;
                        }
                        case ']', '}' -> {
                            if(tags.isSet(AFTER_COMMA))throw new IllegalArgumentException(Exceptions.unexpectedClosing(c, file, i));
                            else status = ParserFunction.closing(tags, AFTER_COMMA, deque, c, file, i, CURRENT_NAMELESS, c == '}');
                        }
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
                                else status = Status.VALUE_UNKNOWN;
                                tags.enable(AFTER_COMMA);
                            }
                        }
                        case ']', '}' -> {
                            if(tags.isSet(AFTER_COMMA))throw new IllegalArgumentException(Exceptions.unexpectedClosing(c, file, i));
                            else status = ParserFunction.closing(tags, AFTER_COMMA, deque, c, file, i, CURRENT_NAMELESS, c == '}');
                        }
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
                        case ':' -> status = Status.VALUE_UNKNOWN;
                        default -> throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                    }
                }
                case VALUE_UNKNOWN -> {
                    switch(c){
                        case ' ', '\n' -> {}
                        case '"' -> {
                            ParserFunction.VALUE_UNKNOWN_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    JsonValue.Type.STRING,
                                    null
                            );
                            tags.disable(AFTER_COMMA);
                            status = Status.VALUE_STRING;
                        }
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' -> {
                            ParserFunction.VALUE_UNKNOWN_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    JsonValue.Type.NUMBER,
                                    null
                            );
                            sb.append(c);
                            tags.disable(AFTER_COMMA);
                            status = Status.VALUE_NUMBER;
                        }
                        case '{' -> {
                            ParserFunction.VALUE_UNKNOWN_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    new HashMap<String, JsonValue>(),
                                    JsonValue.Type.OBJECT,
                                    null
                            );
                            tags.disable(CURRENT_NAMELESS);
                            tags.disable(AFTER_COMMA);
                            status = Status.NONE;
                        }
                        case '[' -> {
                            ParserFunction.VALUE_UNKNOWN_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    new ArrayList<JsonValue>(),
                                    JsonValue.Type.ARRAY,
                                    null
                            );
                            tags.enable(CURRENT_NAMELESS);
                            tags.disable(AFTER_COMMA);
                            status = Status.VALUE_UNKNOWN;
                        }
                        case 't', 'f' -> {
                            ParserFunction.VALUE_UNKNOWN_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    JsonValue.Type.BOOLEAN,
                                    null
                            );
                            if(c == 't')status = Status.VALUE_BOOLEAN_TRUE;
                            else status = Status.VALUE_BOOLEAN_FALSE;
                            tags.disable(AFTER_COMMA);
                        }
                        case 'n' -> {
                            ParserFunction.VALUE_UNKNOWN_ifNamelessPush(
                                    tags,
                                    START,
                                    CURRENT_NAMELESS,
                                    deque,
                                    JsonValue.Type.NULL,
                                    null
                            );
                            tags.disable(AFTER_COMMA);
                            status = Status.VALUE_NULL;
                        }
                        case '}', ']' -> throw new IllegalArgumentException(Exceptions.unexpectedClosing(c, file, i));
                        default ->  throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                    }
                }
                case VALUE_STRING -> {
                    switch(c){
                        case '"' -> {
                            if(tags.isSet(AFTER_BACKSLASH)){
                                tags.disable(AFTER_BACKSLASH);
                                sb.append(c);
                            }else{
                                deque.element().setValue(sb.toString());
                                deque.pop();
                                sb.setLength(0);
                                status = Status.AFTER_STATEMENT;
                            }
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
                        case ' ', ',', ']', '}', '\n' -> {
                            if(isIllegalJsonNumber(sb.toString()))throw new IllegalArgumentException(Exceptions.invalidJsonNumber(file, i, sb.toString()));
                            else{
                                deque.element().setValue(new BigDecimal(sb.toString()));
                                deque.pop();
                                sb.setLength(0);
                                status = ParserFunction.finishValue(tags, AFTER_COMMA, deque, c, file, i, CURRENT_NAMELESS);
                                if(c != ']' && c != '}')i--;
                            }
                        }
                        default ->  throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                    }
                }
                case VALUE_BOOLEAN_TRUE, VALUE_BOOLEAN_FALSE, VALUE_NULL -> {
                    switch(c){
                        case 't', 'r', 'u', 'e', 'f', 'a', 'l', 's', 'n' -> sb.append(c);
                        case ' ', ',', '}', ']', '\n' -> {
                            if(!sb.toString().equals(status.sample))throw new IllegalArgumentException(Exceptions.invalidToken(file, i, sb.toString()));
                            else{
                                deque.element().setValue(status.value);
                                deque.pop();
                                sb.setLength(0);
                                status = ParserFunction.finishValue(tags, AFTER_COMMA, deque, c, file, i, CURRENT_NAMELESS);
                                if(c != ']' && c != '}')i--;
                            }
                        }
                        default ->  throw new IllegalArgumentException(Exceptions.unexpectedChar(c, file, i));
                    }
                }
            }
        }
        switch(status){
            case NAME, AFTER_NAME, VALUE_UNKNOWN, VALUE_STRING -> throw new IllegalArgumentException(Exceptions.unexpectedEOF(file, i));
            case VALUE_NUMBER -> {
                if(isIllegalJsonNumber(sb.toString()))throw new IllegalArgumentException(Exceptions.invalidJsonNumber(file, i, sb.toString()));
                else{
                    deque.element().setValue(new BigDecimal(sb.toString()));
                    deque.pop();
                    sb.setLength(0);
                }
            }
            case VALUE_BOOLEAN_TRUE, VALUE_BOOLEAN_FALSE, VALUE_NULL -> {
                if(!sb.toString().equals(status.sample))throw new IllegalArgumentException(Exceptions.invalidToken(file, i, sb.toString()));
                else{
                    deque.element().setValue(status.value);
                    deque.pop();
                    sb.setLength(0);
                }
            }
            case NONE, AFTER_STATEMENT -> {}
        }
        if(deque.size() != 1 || !deque.peek().isRoot())throw new IllegalArgumentException(
                "Expected closing at " +
                        Strings.toLineCharFormat(file, i) +
                        " (index " + i + ")"
        );
        if(!sb.isEmpty() || tags.isSet(AFTER_BACKSLASH, AFTER_COMMA))throw new IllegalArgumentException(Exceptions.unexpectedEOF(file, i));
        this.box = root;
    }
    private static final class ParserFunction{
        private ParserFunction(){}
        public static Status finishValue(
                BitMask tags,
                int AFTER_COMMA,
                Deque<JsonValue> deque,
                char c,
                String file,
                int i,
                int CURRENT_NAMELESS
        ){
            if(c == ']' || c == '}'){
                return ParserFunction.closing(tags, AFTER_COMMA, deque, c, file, i, CURRENT_NAMELESS, c == '}');
            }else{
                return Status.AFTER_STATEMENT;
            }
        }
        public static void VALUE_UNKNOWN_ifNamelessPush(
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
        public static void VALUE_UNKNOWN_ifNamelessPush(
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
        public static Status closing(
                BitMask tags,
                int AFTER_COMMA,
                Deque<JsonValue> deque,
                char c,
                String file,
                int i,
                int CURRENT_NAMELESS,
                boolean isObject
        ){
            if(tags.isSet(AFTER_COMMA) || (isObject ? !deque.element().isObject() : !deque.element().isArray()))
                throw new IllegalArgumentException(Exceptions.unexpectedClosing(c, file, i));
            else{
                JsonValue value = deque.pop();
                if(deque.peek() != null)if(value.getParent() != null && value.getParent().isArray()){
                    tags.enable(CURRENT_NAMELESS);
                    return Status.VALUE_UNKNOWN;
                }else{
                    tags.disable(CURRENT_NAMELESS);
                    return Status.NONE;
                }
            }
            return null;
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
        public static String invalidToken(String file, int i, String token){
            return "Invalid token at " +
                    Strings.toLineCharFormat(file, i) +
                    " (index " + i + ") -> " +
                    token;
        }
        public static String unexpectedClosing(char c, String file, int i){
            return "Unexpected closing '" + c + "' at " +
                    Strings.toLineCharFormat(file, i) +
                    " (index " + i + ")";
        }
        public static String unexpectedEOF(String file, int i){
            return "Unexpected end of file at " +
                    Strings.toLineCharFormat(file, i) +
                    " (index " + i + ")";
        }
    }
    public boolean isWritable(){
        return Files.isWritable(file);
    }
    public boolean isReadable(){
        return Files.isReadable(file);
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
