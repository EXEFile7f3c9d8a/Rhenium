package dev.exefile7f.rheniumcore.api.exceptions.input;

public class UnexpectedEOFException extends IllegalArgumentException{
    public UnexpectedEOFException(){
        super();
    }
    public UnexpectedEOFException(String message){
        super(message);
    }
    public UnexpectedEOFException(String message, Throwable cause) {
        super(message, cause);
    }
    public UnexpectedEOFException(Throwable cause) {
        super(cause);
    }
}
