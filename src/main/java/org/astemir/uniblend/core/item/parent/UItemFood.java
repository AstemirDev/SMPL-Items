package org.astemir.uniblend.core.item.parent;


import com.google.gson.JsonObject;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.LoadType;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class UItemFood extends UItem {

    @Property(value = "food-effects",type = PotionEffect.class,load = LoadType.LIST)
    private List<PotionEffect> foodEffects = new ArrayList<>();
    @Property("saturation")
    private double saturation = 0.0;
    @Property("hunger")
    private int hunger = 1;

    @Override
    public EventExecutionResult onConsume(PlayerItemConsumeEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setItem(e.getItem().subtract(1));
        }
        for (PotionEffect foodEffect : foodEffects) {
            e.getPlayer().addPotionEffect(foodEffect);
        }
        e.getPlayer().setSaturation((float)(e.getPlayer().getSaturation()+saturation));
        e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel()+hunger);
        return super.onConsume(e);
    }
}
