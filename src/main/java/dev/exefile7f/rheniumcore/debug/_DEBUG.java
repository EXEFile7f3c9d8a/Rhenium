package dev.exefile7f.rheniumcore.debug;

import dev.exefile7f.rheniumcore.util.json.Json;
import dev.exefile7f.rheniumcore.util.json.JsonValue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static dev.exefile7f.rheniumcore.util.json.Jsons.isIllegalJsonNumber;
import static dev.exefile7f.rheniumcore.util.Strings.BooleansToString;

class _DEBUG{
    /**
     * THIS CLASS AND FUNCTION IS NOT FOR ANYTHING BUT ONLY TEMPORARY DEBUG
     * <p>
     * YOU SHOULD NEVER CALL THIS FUNCTION IN YOUR CODE
     */
    public static void main(String[] args) throws Exception{
        long start = System.nanoTime();
        System.out.println(
                BooleansToString(
                        //legals
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
                        //illegals
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
        long end = System.nanoTime();
        System.out.println((end - start) / 25 + " ns");
        {
            Json json;
            {
                start = System.nanoTime();
                json = new Json(Path.of("F:\\Programs\\Code\\Java\\IDE\\rhenium\\main\\src\\main\\java\\dev\\exefile7f\\rheniumcore\\debug\\_DEBUG.json"));
                System.out.println(json.read().toString());
                end = System.nanoTime();
                System.out.println((end - start) / 25 + " ns");
            }
            {
                JsonValue temp = json.get("map").get("array").get(4);
                System.out.println(temp);
                temp.setValue(!temp.toString().equals("true"));
                json.write();
            }
        }


        temp:
        try(InputStream inputStream = _DEBUG.class.getResourceAsStream("/fabric.mod.json")){
            if(inputStream == null){
                System.out.println("Error: File not found in resources!");
                break temp;
            }
            String fileContent = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            System.out.println("Resource content:\n" + fileContent);

        }catch(Exception e){
            e.printStackTrace();
        }
        {
            Json json = new Json(Path.of(_DEBUG.class.getResource("/fabric.mod.json").toURI()));
            json.read();
            System.out.println(json);
            System.out.println(json.get("version").getAsString());
        }
    }
}
