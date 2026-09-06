package dev.exefile7f.rheniumcore.api.exceptions.json;

public class IllegalJsonNumberException extends IllegalArgumentException{
    public IllegalJsonNumberException(){
        super();
    }
    public IllegalJsonNumberException(String message){
        super(message);
    }
    public IllegalJsonNumberException(String message, Throwable cause) {
        super(message, cause);
    }
    public IllegalJsonNumberException(Throwable cause) {
        super(cause);
    }
}
