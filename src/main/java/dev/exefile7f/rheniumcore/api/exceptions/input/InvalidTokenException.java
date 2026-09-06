package dev.exefile7f.rheniumcore.api.exceptions.input;

public class InvalidTokenException extends IllegalArgumentException{
    public InvalidTokenException(){
        super();
    }
    public InvalidTokenException(String message){
        super(message);
    }
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidTokenException(Throwable cause) {
        super(cause);
    }
}
