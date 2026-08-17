package dev.exefile7f.rheniumcore;

import static dev.exefile7f.rheniumcore.util.Jsons.isIllegalJsonNumber;
import static dev.exefile7f.rheniumcore.util.Strings.BooleansToString;

class _DEBUG{
    /**
     * THIS CLASS AND FUNCTION IS NOT FOR ANYTHING BUT ONLY TEMPORARY DEBUG
     * <p>
     * YOU SHOULD NEVER CALL THIS FUNCTION IN YOUR CODE
     */
    public static void main(String[] args){
        System.out.println(
                BooleansToString(
                        isIllegalJsonNumber("0"),
                        isIllegalJsonNumber("-0"),
                        isIllegalJsonNumber("1"),
                        isIllegalJsonNumber("123"),
                        isIllegalJsonNumber("-123"),

                        isIllegalJsonNumber("0.0"),
                        isIllegalJsonNumber("-0.5"),
                        isIllegalJsonNumber("123.000"),

                        isIllegalJsonNumber("1e10"),
                        isIllegalJsonNumber("1e+10"),
                        isIllegalJsonNumber("1e-10"),

                        isIllegalJsonNumber("1.2e10"),
                        isIllegalJsonNumber("1.2e+10"),
                        isIllegalJsonNumber("1.2e-10"),

                        isIllegalJsonNumber("01"),
                        isIllegalJsonNumber("-01"),
                        isIllegalJsonNumber("00"),
                        isIllegalJsonNumber("-00"),
                        isIllegalJsonNumber("-"),
                        isIllegalJsonNumber("1."),
                        isIllegalJsonNumber("1.e"),
                        isIllegalJsonNumber("1.e1"),
                        isIllegalJsonNumber("1e"),
                        isIllegalJsonNumber("1e+"),
                        isIllegalJsonNumber("1e+0.5")
                )
        );
    }
}
