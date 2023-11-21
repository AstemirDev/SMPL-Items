package org.astemir.uniblend.core.particle;

import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Registered;
import org.astemir.uniblend.core.UniblendRegistry;

import java.util.List;

@Registered("particles")
public class UniblendParticleEffects extends UniblendRegistry.Default<UParticleEffect>{

    public static UniblendParticleEffects INSTANCE;
    public UniblendParticleEffects() {
        INSTANCE = this;
    }

    @Override
    public void onConfigLoad(List<PluginJsonConfig> configs) {
        for (PluginJsonConfig config : configs) {
            JsonObject map = config.json();
            for (String setName : map.keySet()) {
                INSTANCE.register(setName, USerialization.get(map,setName, UParticleEffect.class));
            }
        }
    }
}
