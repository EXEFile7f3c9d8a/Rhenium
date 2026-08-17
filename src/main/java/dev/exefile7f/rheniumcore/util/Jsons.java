package dev.exefile7f.rheniumcore.util;

public final class Jsons{
    private Jsons(){}
    public static boolean isIllegalJsonNumber(String number){
        enum Status{
            START,
            INTEGERS,
            DECIMAL,
            EXPONENT,
        }
        int AFTER_NEGATIVE = 1 << 0;
        int START_WITH_0 = 1 << 3;
        int AFTER_EXPONENT = 1 << 1;
        int AFTER_DECIMAL = 1 << 2;
        int AFTER_EXPONENT_SIGNS = 1 << 4;
        int AFTER_EXPONENT_NUMBERS = 1 << 5;
        int tags = 0;
        Status status = Status.START;
        int i = 0;
        for(; i < number.length(); i++){
            char c = number.charAt(i);
            switch(status){
                case START -> {
                    switch(c){
                        case '-' -> {
                            tags |= AFTER_NEGATIVE;
                            status = Status.INTEGERS;
                        }
                        case '0' -> {
                            tags |= START_WITH_0;
                            status = Status.INTEGERS;
                        }
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> status = Status.INTEGERS;
                        default -> {return true;}
                    }
                }
                case INTEGERS -> {
                    switch(c){
                        case '0' -> {
                            if((tags & START_WITH_0) != 0)return true;
                            if((tags & AFTER_NEGATIVE) != 0){
                                tags |= START_WITH_0;
                                tags &= ~AFTER_NEGATIVE;
                            }
                        }
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            if((tags & AFTER_NEGATIVE) != 0)tags &= ~AFTER_NEGATIVE;
                            if((tags & START_WITH_0) != 0)return true;
                        }
                        case '.' -> {
                            if((tags & AFTER_NEGATIVE) != 0)return true;
                            else{
                                tags |= AFTER_DECIMAL;
                                status = Status.DECIMAL;
                            }
                        }
                        case 'e', 'E' -> {
                            if((tags & AFTER_NEGATIVE) != 0)return true;
                            tags |= AFTER_EXPONENT;
                            status = Status.EXPONENT;
                        }
                        default -> {return true;}
                    }
                }
                case DECIMAL -> {
                    switch(c){
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> tags &= ~AFTER_DECIMAL;
                        case 'e', 'E' -> {
                            if((tags & AFTER_DECIMAL) != 0)return true;
                            else{
                                tags |= AFTER_EXPONENT;
                                tags &= ~AFTER_DECIMAL;
                                status = Status.EXPONENT;
                            }
                        }
                        default -> {return true;}
                    }
                }
                case EXPONENT -> {
                    switch(c){
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            tags &= ~AFTER_EXPONENT;
                            tags &= ~AFTER_EXPONENT_SIGNS;
                            tags |= AFTER_EXPONENT_NUMBERS;
                        }
                        case '-', '+' -> {
                            if((tags & AFTER_EXPONENT_NUMBERS) != 0 || (tags & AFTER_EXPONENT_SIGNS) != 0)return true;
                            else{
                                tags &= ~AFTER_EXPONENT;
                                tags |= AFTER_EXPONENT_SIGNS;
                            }
                        }
                        default -> {return true;}
                    }
                }
            }
        }
        return  (tags & AFTER_NEGATIVE) != 0 ||
                (tags & AFTER_DECIMAL) != 0 ||
                (tags & AFTER_EXPONENT) != 0 ||
                (tags & AFTER_EXPONENT_SIGNS) != 0;
    }
}
