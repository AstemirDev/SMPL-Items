package org.astemir.uniblend.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.core.command.UReloadCommand;
import org.astemir.uniblend.event.UniblendEventListener;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class UModules extends UniblendModule implements UniblendRegistry<UniblendModule> {
    private List<UniblendModule> modules = new ArrayList<>();

    public UModules(UniblendModule... modules) {
        addAll(modules);
        for (UniblendModule module : modules) {
            module.setupLookups();
        }
    }

    @Override
    public void onRegister() {
        enableAllModules(true);
        registerCommand(new UReloadCommand());
        registerEvent(new UniblendEventListener());
    }

    @Override
    public void onDisable() {
        unloadAllModules();
    }

    @Override
    public void onUpdate(long tick) {
        for (UniblendModule module : modules) {
            if (module.isEnabled()) {
                module.update(tick);
            }
        }
    }

    @Override
    public void onUpdatePerPlayer(Player player, long tick) {
        for (UniblendModule module : modules) {
            if (module.isEnabled()) {
                module.updatePerPlayer(player, tick);
            }
        }
    }

    public void enableAllModules(boolean firstLoad){
        PluginJsonConfig config = new PluginJsonConfig(UniblendCorePlugin.getPlugin(),"config.json",true);
        for (UniblendModule module : modules) {
            Registered registered = module.getClass().getAnnotation(Registered.class);
            if (registered != null) {
                JsonObject moduleJson = config.getJsonObject("modules/"+registered.value());
                boolean enabled = true;
                if (moduleJson.has("enabled")){
                    enabled = moduleJson.get("enabled").getAsBoolean();
                }
                List<PluginJsonConfig> configs = new ArrayList<>();
                if (enabled) {
                    JsonArray configurationsJsonList = moduleJson.get("configurations").getAsJsonArray();
                    for (JsonElement jsonElement : configurationsJsonList) {
                        JsonObject configJson = jsonElement.getAsJsonObject();
                        String path = configJson.get("path").getAsString();
                        PluginJsonConfig moduleConfig = new PluginJsonConfig(UniblendCorePlugin.getPlugin(), path, true);
                        configs.add(moduleConfig);
                    }
                }
                if (enabled) {
                    if (firstLoad) {
                        module.register();
                    }
                    module.enable();
                    module.configLoad(configs);
                }
            }else{
                if (firstLoad) {
                    module.register();
                }
                module.enable();
            }
        }
    }

    public void unloadAllModules(){
        for (UniblendModule module : modules) {
            module.disable();
        }
    }

    @Override
    public UniblendModule add(UniblendModule instance) {
        modules.add(instance);
        return instance;
    }

    @Override
    public List<UniblendModule> getEntries() {
        return modules;
    }

    public static UModules getInstance(){
        return UniblendCorePlugin.getPlugin().getModules();
    }
}
