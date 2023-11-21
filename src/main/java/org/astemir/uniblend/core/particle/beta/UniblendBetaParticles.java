package org.astemir.uniblend.core.particle.beta;


import com.google.gson.JsonObject;
import org.astemir.uniblend.core.Registered;
import org.astemir.uniblend.core.UniblendRegistry;
import org.astemir.uniblend.core.particle.beta.command.UParticleCommand;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.astemir.uniblend.io.json.USerialization;
import java.util.List;

@Registered("beta-particles")
public class UniblendBetaParticles extends UniblendRegistry.Default<BetaParticleEmitter>{

    public static UniblendBetaParticles INSTANCE;
    public UniblendBetaParticles() {
        INSTANCE = this;
    }
    @Override
    public void onRegister() {
        registerCommand(new UParticleCommand());
    }

    @Override
    public void onConfigLoad(List<PluginJsonConfig> configs) {
        clear();
        for (PluginJsonConfig config : configs) {
            JsonObject map = config.json();
            for (String name : map.keySet()) {
                INSTANCE.register(name, USerialization.get(map,name, BetaParticleEmitter.class));
            }
        }
    }

    public static BetaParticleEmitter spawnEmitter(String name){
        BetaParticleEmitter emitter = INSTANCE.getEntry(name).create();
        UniblendBetaParticlesHandler.INSTANCE.add(emitter);
        return emitter;
    }
}
