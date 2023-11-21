package org.astemir.uniblend.core.attribute;

import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Registered;
import org.astemir.uniblend.core.UniblendRegistry;

import java.util.List;


@Registered("item-attributes")
public class UniblendAttributes extends UniblendRegistry.Default<UItemAttribute>{

    public static UniblendAttributes INSTANCE;
    public UniblendAttributes() {
        INSTANCE = this;
    }

    @Override
    public void onConfigLoad(List<PluginJsonConfig> configs) {
        clear();
        for (PluginJsonConfig config : configs) {
            JsonObject map = config.json();
            for (String setName : map.keySet()) {
                INSTANCE.register(setName, USerialization.deserialize(map.get(setName), UItemAttribute.class));
            }
        }
    }
}
