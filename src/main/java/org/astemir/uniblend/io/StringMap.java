package org.astemir.uniblend.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;

import java.util.HashMap;

public class StringMap extends HashMap<String,String> {

    public static UJsonDeserializer<StringMap> DESERIALIZER = (json)->{
      if (json.isJsonObject()){
          JsonObject jsonObject = json.getAsJsonObject();
          StringMap map = new StringMap();
          for (String key : jsonObject.keySet()) {
              JsonElement element = jsonObject.get(key);
              map.put(key, element.getAsString());
          }
          return map;
      }
      return new StringMap();
    };


}
