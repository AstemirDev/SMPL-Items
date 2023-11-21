package org.astemir.uniblend.core.command;

import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Registered;
import org.astemir.uniblend.core.UniblendRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

@Registered("blocked-commands")
public class UBlockedCommandHandler extends UniblendRegistry.Default<UBlockedCommand> implements Listener {

    public static UBlockedCommandHandler INSTANCE;
    public UBlockedCommandHandler() {
        INSTANCE = this;
    }

    @Override
    public void onRegister() {
        registerEvent(this);
    }

    @Override
    public void onConfigLoad(List<PluginJsonConfig> configs) {
        clear();
        for (PluginJsonConfig config : configs) {
            JsonObject map = config.json();
            for (String command : map.keySet()) {
                INSTANCE.register(command, USerialization.get(map,command, UBlockedCommand.class));
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        if (!e.getMessage().isEmpty() && e.getMessage().charAt(0) == '/') {
            String command = e.getMessage().split(" ")[0].substring(1);
            for (UBlockedCommand entry : getEntries()) {
                if (entry.isValid(command)){
                    e.getPlayer().sendMessage(entry.getErrorMessage());
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
}
