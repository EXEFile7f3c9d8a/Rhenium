package dev.exefile7f.rheniumcore.api.exceptions;

public class UnexpectedLineBreakException extends IllegalArgumentException{
    public UnexpectedLineBreakException(){
        super();
    }
    public UnexpectedLineBreakException(String message){
        super(message);
    }
    public UnexpectedLineBreakException(String message, Throwable cause) {
        super(message, cause);
    }
    public UnexpectedLineBreakException(Throwable cause) {
        super(cause);
    }
}
