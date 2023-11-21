package org.astemir.uniblend.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.kyori.adventure.text.format.TextColor;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomizedColor {

    public static final UJsonDeserializer<RandomizedColor> DESERIALIZER = (json)->{
        if (json.isJsonArray()){
            List<TextColor> list = new ArrayList<>();
            JsonArray array = json.getAsJsonArray();
            for (JsonElement element : array) {
                list.add(USerialization.deserialize(element,TextColor.class));
            }
            return new RandomizedColor(list.toArray(new TextColor[list.size()]));
        }else{
            return new RandomizedColor(USerialization.deserialize(json,TextColor.class));
        }
    };

    private List<TextColor> colors;

    public RandomizedColor(TextColor... colors) {
        this.colors = Arrays.asList(colors);
    }

    public TextColor get(){
        if (colors.size() > 1) {
            return colors.get(RandomUtils.randomInt(colors.size() ));
        }else{
            return colors.get(0);
        }
    }
}
