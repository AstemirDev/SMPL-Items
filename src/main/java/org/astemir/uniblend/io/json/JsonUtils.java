package org.astemir.uniblend.io.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.astemir.uniblend.io.json.USerialization;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static List<String> listString(JsonArray array){
        List<String> list = new ArrayList<>();
        for (JsonElement jsonElement : array) {
            list.add(jsonElement.getAsString());
        }
        return list;
    }

    public static <T> List<T> list(JsonArray array,Class<T> className){
        List<T> list = new ArrayList<>();
        for (JsonElement jsonElement : array) {
            list.add(USerialization.as(jsonElement,className));
        }
        return list;
    }
}
