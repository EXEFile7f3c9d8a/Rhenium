package dev.exefile7f.rheniumcore.api.exceptions.json;

public class UnexpectedClosingException extends IllegalArgumentException{
    public UnexpectedClosingException(){
        super();
    }
    public UnexpectedClosingException(String message){
        super(message);
    }
    public UnexpectedClosingException(String message, Throwable cause) {
        super(message, cause);
    }
    public UnexpectedClosingException(Throwable cause) {
        super(cause);
    }
}
