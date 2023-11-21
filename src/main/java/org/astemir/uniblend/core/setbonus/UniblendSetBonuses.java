package org.astemir.uniblend.core.setbonus;

import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Registered;
import org.astemir.uniblend.core.UniblendRegistry;
import org.astemir.uniblend.core.setbonus.event.EventListenerSetBonus;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;


@Registered("set-bonuses")
public class UniblendSetBonuses extends UniblendRegistry.Default<USetBonus>{

    public static UniblendSetBonuses INSTANCE;

    public UniblendSetBonuses() {
        INSTANCE = this;
    }

    @Override
    public void onRegister() {
        registerEvent(new EventListenerSetBonus());
    }

    @Override
    public void onConfigLoad(List<PluginJsonConfig> configs) {
        clear();
        for (PluginJsonConfig config : configs) {
            JsonObject map = config.json();
            for (String setName : map.keySet()) {
                INSTANCE.register(setName, USerialization.deserialize(map.getAsJsonObject(setName), USetBonus.class));
            }
        }
    }

    @Override
    public void onUpdatePerPlayer(Player player, long tick) {
        List<String> activeSetBonuses = new ArrayList<>();
        getEntries().forEach((bonus)->{
            if (tick % 5 == 0) {
                if (!activeSetBonuses.contains(bonus.getNameKey())) {
                    activeSetBonuses.add(bonus.getNameKey());
                    bonus.affect(player);
                }
            }
        });
    }
}
