package org.astemir.uniblend.core.projectile;

import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Registered;
import org.astemir.uniblend.core.UniblendRegistry;
import org.astemir.uniblend.core.projectile.builtin.BloodPact;

import java.util.List;

@Registered("projectiles")
public class UniblendProjectiles extends UniblendRegistry.Default<UProjectile>{

    public static UniblendProjectiles INSTANCE;
    public UniblendProjectiles() {
        INSTANCE = this;
    }

    @Override
    public void onSetupLookups() {
        setLookup("projectile", UProjectile.class);
        setLookup("blood_pact", BloodPact.class);
    }

    @Override
    public void onConfigLoad(List<PluginJsonConfig> configs) {
        clear();
        configs.forEach((config)->{
            JsonObject map = config.json();
            for (String setName : map.keySet()) {
                INSTANCE.register(setName, USerialization.deserialize(map.get(setName), UProjectile.class));
            }
        });
    }
}
