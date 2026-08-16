package dev.exefile7f.rheniumcore.util;

public final class Strings{
    public static String getNearCharacters(String str, int pos, int radius){
        return str.substring(
                Math.max(0, pos - radius),
                Math.min(str.length(), pos + radius + 1)
        );
    }
    public static String toLineCharFormat(String str, int pos){
        int lines = 0;
        int character = 0;
        int i = 0;
        for(; i < pos; i++){
            if(str.charAt(i) == '\n')lines++;
        }
        for(i = pos - 1; i >= 0 && str.charAt(i) != '\n'; i--){
            character++;
        }
        return lines + ":" + character;
    }
}
