package dev.exefile7f.rheniumcore.api.util;

public final class Booleans{
    private Booleans(){}
    public static String toString(boolean... bool){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < bool.length; i++){
            sb.append(bool[i]).append(" ");
        }
        return sb.toString();
    }
}
