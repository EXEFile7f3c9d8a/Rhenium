package dev.exefile7f.rheniumcore.debug;

import dev.exefile7f.rheniumcore.util.Timer;
import dev.exefile7f.rheniumcore.util.json.Json;
import dev.exefile7f.rheniumcore.util.json.JsonValue;

import java.nio.file.Path;

class _DEBUG{
    /**
     * THIS CLASS AND FUNCTION IS NOT FOR ANYTHING BUT ONLY TEMPORARY DEBUG
     * <p>
     * YOU SHOULD NEVER CALL THIS FUNCTION IN YOUR CODE
     */
    public static void main(String[] args) throws Exception{
        Timer timer = new Timer();
//        timer.start();
//        System.out.println(
//                BooleansToString(
//                        //legals
//                        isIllegalJsonNumber("0"),
//                        isIllegalJsonNumber("-0"),
//                        isIllegalJsonNumber("1"),
//                        isIllegalJsonNumber("123"),
//                        isIllegalJsonNumber("-123"),
//
//                        isIllegalJsonNumber("0.0"),
//                        isIllegalJsonNumber("-0.5"),
//                        isIllegalJsonNumber("123.000"),
//
//                        isIllegalJsonNumber("1e10"),
//                        isIllegalJsonNumber("1e+10"),
//                        isIllegalJsonNumber("1e-10"),
//
//                        isIllegalJsonNumber("1.2e10"),
//                        isIllegalJsonNumber("1.2e+10"),
//                        isIllegalJsonNumber("1.2e-10"),
//                        //illegals
//                        isIllegalJsonNumber("01"),
//                        isIllegalJsonNumber("-01"),
//                        isIllegalJsonNumber("00"),
//                        isIllegalJsonNumber("-00"),
//                        isIllegalJsonNumber("-"),
//                        isIllegalJsonNumber("1."),
//                        isIllegalJsonNumber("1.e"),
//                        isIllegalJsonNumber("1.e1"),
//                        isIllegalJsonNumber("1e"),
//                        isIllegalJsonNumber("1e+"),
//                        isIllegalJsonNumber("1e+0.5")
//                )
//        );
//        System.out.println(timer.stop());
        {
            Json json = new Json(Path.of("F:\\Programs\\Code\\Java\\IDE\\rhenium\\main\\src\\main\\java\\dev\\exefile7f\\rheniumcore\\debug\\_DEBUG.json"));
            {
                System.out.println("File size:" + json.fileSize());
                timer.start();
                json.syncFile();
                System.out.println("IO time:" + timer.stop());
                timer.start();
                json.read();
                System.out.println("parse time:" + timer.stop());
                timer.start();
                System.out.println(json.toString());
                System.out.println("rephrase time:" + timer.stop());
            }
            {
                //stress test
                for (int i = 0; i < 2000; i++) {
                    json.read();
                }
                long start = System.nanoTime();
                for (int i = 0; i < 100_000; i++) {
                    json.read();
                }
                long end = System.nanoTime();
                double ms = (end - start) / 1_000_000.0;
                System.out.println("total = " + ms + " ms");
                System.out.println("average = " + ms / 10000 + " ms");
            }
//            {
//                JsonValue temp = json.get("1map").get("array").get(4);
//                System.out.println(temp);
//                temp.setValue(!temp.toString().equals("true"));
//                json.write();
//            }
        }
//        temp:
//        try(InputStream inputStream = _DEBUG.class.getResourceAsStream("/fabric.mod.json")){
//            if(inputStream == null){
//                System.out.println("Error: File not found in resources!");
//                break temp;
//            }
//            String fileContent = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
//                    .lines()
//                    .collect(Collectors.joining("\n"));
//
//            System.out.println("Resource content:\n" + fileContent);
//
//        }catch(Exception e){
//            throw new RuntimeException(e);
//        }
//        {
//            Json json = new Json(Path.of(_DEBUG.class.getResource("/fabric.mod.json").toURI()));
//            json.read();
//            System.out.println(json);
//            System.out.println(json.get("version").getAsString());
//        }
    }
}
