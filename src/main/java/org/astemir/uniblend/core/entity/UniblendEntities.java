package org.astemir.uniblend.core.entity;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.entity.parent.UEntity;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Registered;
import org.astemir.uniblend.core.UniblendRegistry;
import org.astemir.uniblend.core.entity.command.UEntityCommand;
import org.bukkit.Location;

import java.util.List;


@Registered("entities")
public class UniblendEntities extends UniblendRegistry.Default<UEntity>{

    public static UniblendEntities INSTANCE;
    public UniblendEntities() {
        INSTANCE = this;
    }
    @Override
    public void onRegister() {
        registerCommand(new UEntityCommand());
    }

    @Override
    public void onConfigLoad(List<PluginJsonConfig> configs) {
        clear();
        for (PluginJsonConfig config : configs) {
            JsonObject map = config.json();
            for (String setName : map.keySet()) {
                INSTANCE.register(setName, USerialization.deserialize(map.get(setName), UEntity.class));
            }
        }
    }


    public static UEntity spawn(String id, Location location){
        UEntity entity = INSTANCE.matchEntry(id).create();
        UEntityHandler.INSTANCE.register(id,entity);
        entity.spawn(location);
        return entity;
    }
}
