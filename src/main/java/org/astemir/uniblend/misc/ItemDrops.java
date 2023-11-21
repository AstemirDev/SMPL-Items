package org.astemir.uniblend.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDrops {

    public static final UJsonDeserializer<ItemDrops> DESERIALIZER = (json)->{
        if (json.isJsonArray()){
            JsonArray array = json.getAsJsonArray();
            ItemDrops drops = new ItemDrops();
            for (JsonElement jsonElement : array) {
                if (jsonElement.isJsonObject()){
                    JsonObject dropJson = jsonElement.getAsJsonObject();
                    if (dropJson.has("chance")){
                        ItemComponent component = USerialization.get(dropJson, "item",ItemComponent.class);
                        int chance = dropJson.get("chance").getAsInt();
                        drops.addDrop(component,chance);
                    }else{
                        drops.addDrop(USerialization.as(dropJson, ItemComponent.class),100);
                    }
                }
            }
            return drops;
        }
        return new ItemDrops();
    };

    private Map<ItemComponent,Integer> drops = new HashMap<>();

    public void addDrop(ItemComponent component,int chance){
        drops.put(component,chance);
    }

    public void spawnDrops(Location location){
        for (ItemComponent rollDrop : rollDrops()) {
            location.getWorld().dropItem(location,rollDrop.getItemStack().get()).setVelocity(new Vector(0,0.15f,0));
        }
    }

    private List<ItemComponent> rollDrops(){
        List<ItemComponent> list = new ArrayList<>();
        for (Map.Entry<ItemComponent, Integer> entry : drops.entrySet()) {
            if (entry.getValue() == 100){
                list.add(entry.getKey());
            }else{
                if (RandomUtils.doWithChance(entry.getValue())){
                    list.add(entry.getKey());
                }
            }
        }
        return list;
    }
}
