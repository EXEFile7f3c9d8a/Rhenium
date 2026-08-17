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
        BitMask tags = new BitMask();
        int AFTER_NEGATIVE = tags.create();
        int START_WITH_0 = tags.create();
        int AFTER_EXPONENT = tags.create();
        int AFTER_DECIMAL = tags.create();
        int AFTER_EXPONENT_SIGNS = tags.create();
        int AFTER_EXPONENT_NUMBERS = tags.create();
        Status status = Status.START;
        int i = 0;
        for(; i < number.length(); i++){
            char c = number.charAt(i);
            switch(status){
                case START -> {
                    switch(c){
                        case '-' -> {
                            tags.enable(AFTER_NEGATIVE);
                            status = Status.INTEGERS;
                        }
                        case '0' -> {
                            tags.enable(START_WITH_0);
                            status = Status.INTEGERS;
                        }
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> status = Status.INTEGERS;
                        default -> {return true;}
                    }
                }
                case INTEGERS -> {
                    switch(c){
                        case '0' -> {
                            if(tags.isSet(START_WITH_0))return true;
                            if(tags.isSet(AFTER_NEGATIVE)){
                                tags.enable(START_WITH_0);
                                tags.disable(AFTER_NEGATIVE);
                            }
                        }
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            if(tags.isSet(AFTER_NEGATIVE))tags.disable(AFTER_NEGATIVE);
                            if(tags.isSet(START_WITH_0))return true;
                        }
                        case '.' -> {
                            if(tags.isSet(AFTER_NEGATIVE))return true;
                            else{
                                tags.enable(AFTER_DECIMAL);
                                status = Status.DECIMAL;
                            }
                        }
                        case 'e', 'E' -> {
                            if(tags.isSet(AFTER_NEGATIVE))return true;
                            tags.enable(AFTER_EXPONENT);
                            status = Status.EXPONENT;
                        }
                        default -> {return true;}
                    }
                }
                case DECIMAL -> {
                    switch(c){
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> tags.disable(AFTER_DECIMAL);
                        case 'e', 'E' -> {
                            if(tags.isSet(AFTER_DECIMAL))return true;
                            else{
                                tags.enable(AFTER_EXPONENT);
                                tags.disable(AFTER_DECIMAL);
                                status = Status.EXPONENT;
                            }
                        }
                        default -> {return true;}
                    }
                }
                case EXPONENT -> {
                    switch(c){
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            tags.disable(AFTER_EXPONENT);
                            tags.disable(AFTER_EXPONENT_SIGNS);
                            tags.enable(AFTER_EXPONENT_NUMBERS);
                        }
                        case '-', '+' -> {
                            if(tags.isSet(AFTER_EXPONENT_NUMBERS)|| tags.isSet(AFTER_EXPONENT_SIGNS))return true;
                            else{
                                tags.disable(AFTER_EXPONENT);
                                tags.enable(AFTER_EXPONENT_SIGNS);
                            }
                        }
                        default -> {return true;}
                    }
                }
            }
        }
        return  tags.isSet(AFTER_NEGATIVE) ||
                tags.isSet(AFTER_DECIMAL) ||
                tags.isSet(AFTER_EXPONENT)||
                tags.isSet(AFTER_EXPONENT_SIGNS);
    }
}
