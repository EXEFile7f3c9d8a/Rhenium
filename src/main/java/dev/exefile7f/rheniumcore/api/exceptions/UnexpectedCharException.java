package dev.exefile7f.rheniumcore.api.exceptions;

public class UnexpectedCharException extends IllegalArgumentException{
    public UnexpectedCharException(){
        super();
    }
    public UnexpectedCharException(String message){
        super(message);
    }
    public UnexpectedCharException(String message, Throwable cause) {
        super(message, cause);
    }
    public UnexpectedCharException(Throwable cause) {
        super(cause);
    }
}
