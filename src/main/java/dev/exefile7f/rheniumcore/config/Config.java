package dev.exefile7f.rheniumcore.config;

import dev.exefile7f.rheniumcore.util.ArrayMap;
import dev.exefile7f.rheniumcore.util.json.Json;
import dev.exefile7f.rheniumcore.util.json.JsonValue;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public class Config{
    private final Json json;
    public Config(Path path){
        this.json = new Json(path);
        try{
            this.json.read();
        }catch(IOException e){
            throw new RuntimeException(e);
        }

    }
    public Config save(Object name, Object value){
        if(json.get().isObject())json.get().getAsObject().put(name.toString(), new JsonValue().setValue(value).setName(name.toString()));
        else return null;
        return this;
    }
    public Config tryWrite(Logger logger){
        try{
            json.write();
        }catch(IOException e){
            logger.error(e.toString());
        }
        return null;
    }
    public JsonValue get(String name){
        return json.get(name);
    }
    public Config write() throws IOException{
        json.write();
        return this;
    }
}
