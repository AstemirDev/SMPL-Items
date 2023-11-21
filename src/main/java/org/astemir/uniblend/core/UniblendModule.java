package org.astemir.uniblend.core;

import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.core.command.UCommand;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UniblendModule {

    private Map<String,Class<?>> lookups = new HashMap<>();
    private boolean enabled = false;
    public void onRegister(){}
    public void onEnable(){}
    public void onDisable(){}
    public void onConfigLoad(List<PluginJsonConfig> configs){}
    public void onSetupLookups(){}
    public void onUpdate(long tick){}
    public void onUpdatePerPlayer(Player player,long tick){}

    public void register(){
        onRegister();
    }

    public void enable(){
        enabled = true;
        onEnable();
    }
    public void disable(){
        enabled = false;
        onDisable();
    }

    public void configLoad(List<PluginJsonConfig> configs){
        onConfigLoad(configs);
    }

    public void setupLookups(){
        onSetupLookups();
    }

    public void update(long tick){
        onUpdate(tick);
    }

    public void updatePerPlayer(Player player, long tick){
        onUpdatePerPlayer(player,tick);
    }

    public void setLookup(String key, Class<?> className){
        if (!getLookup().containsKey(key)) {
            getLookup().put(key, className);
        }
    }

    public Class<?> searchClass(String name){
        for (Map.Entry<String, Class<?>> entry : getLookup().entrySet()) {
            if (entry.getKey().toLowerCase().equals(name.toLowerCase())) {
                return entry.getValue();
            }
        }
        for (Map.Entry<String, Class<?>> entry : getLookup().entrySet()) {
            if (entry.getKey().toLowerCase().contains(name.toLowerCase())) {
                return entry.getValue();
            }
        }
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEnabled(){
        return enabled;
    }

    public Map<String,Class<?>> getLookup(){
        return lookups;
    }

    public static void registerCommand(UCommand command){
        command.register();
    }

    public static void registerEvent(Listener listener){
        Bukkit.getPluginManager().registerEvents(listener, UniblendCorePlugin.getPlugin());
    }
}
