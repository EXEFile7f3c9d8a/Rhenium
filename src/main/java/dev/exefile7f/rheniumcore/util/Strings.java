package dev.exefile7f.rheniumcore.util;

public final class Strings{
    private Strings(){}
    public static String getNearCharacters(String str, int pos, int radius){
        return str.substring(
                Math.max(0, pos - radius),
                Math.min(str.length(), pos + radius + 1)
        );
    }
    public static String toLineCharFormat(String str, int pos){
        int lines = 1;
        int character = 1;
        int i = 0;
        for(; i < pos; i++){
            if(str.charAt(i) == '\n')lines++;
        }
        for(i = pos - 1; i >= 0 && str.charAt(i) != '\n'; i--){
            character++;
        }
        return lines + ":" + character;
    }
    public static String BooleansToString(boolean... bool){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < bool.length; i++){
            sb.append(bool[i]).append(" ");
        }
        return sb.toString();
    }
}
