package org.astemir.uniblend.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;

import java.util.HashMap;

public class TextComponentMap extends HashMap<String,Component> {

    public static UJsonDeserializer<TextComponentMap> DESERIALIZER = (json)->{
      if (json.isJsonObject()){
          JsonObject jsonObject = json.getAsJsonObject();
          TextComponentMap map = new TextComponentMap();
          for (String key : jsonObject.keySet()) {
              JsonElement element = jsonObject.get(key);
              map.put(key, USerialization.as(element, Component.class));
          }
          return map;
      }
      return new TextComponentMap();
    };
}
