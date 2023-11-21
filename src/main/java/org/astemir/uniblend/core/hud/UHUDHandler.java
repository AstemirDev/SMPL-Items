package org.astemir.uniblend.core.hud;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.astemir.uniblend.core.UniblendModule;
import org.astemir.uniblend.core.community.UPlayerDataHandler;
import org.astemir.uniblend.core.community.UPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class UHUDHandler extends UniblendModule {

    public static UHUDHandler INSTANCE;
    private static final String[] CLOCK_SPRITE = {
            "\uE800",
            "\uE801",
            "\uE802",
            "\uE803",
            "\uE804",
            "\uE805",
            "\uE806",
            "\uE807",
            "\uE808",
            "\uE809",
            "\uE80A",
            "\uE80B"
    };
    private ConcurrentHashMap<String, BossBar> timeBars = new ConcurrentHashMap<>();

    public UHUDHandler() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (BossBar bossBar : onlinePlayer.activeBossBars()) {
                bossBar.removeViewer(onlinePlayer);
            }
        }
        for (World world : Bukkit.getWorlds()) {
            timeBars.put(world.getName(),BossBar.bossBar(timeConvert(world),1.0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS));
        }
    }

    @Override
    public void onDisable() {
        timeBars.forEach((worldName,bar)->{
            for (Player player : Bukkit.getOnlinePlayers()) {
                bar.removeViewer(player);
            }
        });
        timeBars.clear();
    }

    @Override
    public void onUpdate(long tick) {
        for (World world : Bukkit.getWorlds()) {
            if (timeBars.containsKey(world.getName())){
                timeBars.get(world.getName()).name(timeConvert(world));
            }
        }
    }

    @Override
    public void onUpdatePerPlayer(Player player, long tick) {
        UPlayerData data = UPlayerDataHandler.INSTANCE.getOrCreateData(player);
        if (tick % 40 == 0){
            player.sendPlayerListFooter(Component.text("Ping: "+player.getPing()+"ms | TPS: "+calculateTPS(player)));
        }
        if (data.isShowTime()) {
            timeBars.forEach((world, bar) -> {
                if (player.getWorld().getName().equals(world)) {
                    player.showBossBar(bar);
                } else {
                    player.hideBossBar(bar);
                }
            });
        }
    }

    public void removePlayer(Player player){
        timeBars.forEach((world,bar)->{
            bar.removeViewer(player);
        });
    }

    private Component timeConvert(World world){
        long gameTime = world.getTime();
        long hours = gameTime / 1000L + 6L;
        long minutes = gameTime % 1000L * 60L / 1000L;
        long hours24 = hours % 24;
        long hours12 = hours24 % 12 == 0 ? 12 : hours24 % 12;
        String ampm = hours24 < 12 ? "AM" : "PM";
        String clock = CLOCK_SPRITE[(int) hours12-1];
        return Component.text("§r "+clock+" §f" + (hours12 < 10 ? "0" + hours12 : hours12) + ":" + (minutes < 10 ? "0" + minutes : minutes) + " " + ampm);
    }

    private String calculateTPS(Player player){
        double tps = player.getServer().getTPS()[0];
        if (tps > 19.99){
            tps = 20;
            return String.valueOf((int)tps);
        }
        return String.format(Locale.US, "%.2f", tps);
    }
}
