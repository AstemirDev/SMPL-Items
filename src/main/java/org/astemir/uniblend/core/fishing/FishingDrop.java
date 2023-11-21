package org.astemir.uniblend.core.fishing;

import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.misc.ItemComponent;
import org.astemir.uniblend.misc.ValueRange;
import org.astemir.uniblend.io.json.JsonUtils;
import org.astemir.uniblend.utils.WorldUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class FishingDrop {

    public static final UJsonDeserializer<FishingDrop> DESERIALIZER = (json)->{
        if (json.isJsonObject()){
            JsonObject jsonObject = json.getAsJsonObject();
            FishingDrop drop = new FishingDrop(USerialization.get(jsonObject,"item", ItemComponent.class),jsonObject.get("fish-power").getAsDouble(),jsonObject.get("chance").getAsDouble());
            if (jsonObject.has("conditions")){
                JsonObject conditionsJson = jsonObject.getAsJsonObject("conditions");
                for (String conditionName : conditionsJson.keySet()) {
                    switch (conditionName.toLowerCase()){
                        case "biome":{
                            drop.addPredicate(FishingPredicate.checkBiome(JsonUtils.list(conditionsJson.getAsJsonArray(conditionName), NamespacedKey.class)));
                            break;
                        }
                        case "time":{
                            drop.addPredicate(FishingPredicate.checkTime(USerialization.get(conditionsJson,conditionName, ValueRange.class)));
                            break;
                        }
                        case "depth":{
                            drop.addPredicate(FishingPredicate.checkDepth(USerialization.get(conditionsJson,conditionName, ValueRange.class)));
                            break;
                        }
                    }
                }
            }
            return drop;
        }
        return null;
    };

    private ItemComponent item;
    private double fishPower;
    private double chance;
    private List<FishingPredicate> predicates = new ArrayList<>();

    public FishingDrop(ItemComponent item, double fishPower,double chance) {
        this.item = item;
        this.fishPower = fishPower;
        this.chance = chance;
    }

    public void addPredicate(FishingPredicate predicate){
        predicates.add(predicate);
    }

    public double getChance() {
        return chance;
    }

    public boolean test(Player player,FishHook hook){
        for (FishingPredicate predicate : predicates) {
            if (!predicate.test(player,hook)){
                return false;
            }
        }
        return true;
    }

    public ItemComponent getItem() {
        return item;
    }

    public double getFishPower() {
        return fishPower;
    }

    public interface FishingPredicate{


        static FishingPredicate checkDepth(ValueRange range){
            return (player,fishHook)->{
                int depth = (int) fishHook.getLocation().getY();
                return (int)range.getMinValue() <= depth && depth <= (int)range.getMaxValue();
            };
        }

        static FishingPredicate checkTime(ValueRange range){
            return (player,fishHook)->{
                int hours = WorldUtils.getHours(player.getWorld());
                return (int)range.getMinValue() <= hours && hours<= (int)range.getMaxValue();
            };
        }

        static FishingPredicate checkBiome(List<NamespacedKey> biomeKeys){
            return (player,fishHook)->{
                NamespacedKey currentBiome = WorldUtils.getBiome(fishHook.getLocation());
                for (NamespacedKey key : biomeKeys) {
                    if (currentBiome.equals(key)){
                        return true;
                    }
                }
                return false;
            };
        }

        boolean test(Player player, FishHook fishHook);
    }
}
