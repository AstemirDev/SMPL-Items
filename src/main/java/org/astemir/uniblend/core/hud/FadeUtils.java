package org.astemir.uniblend.core.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public class FadeUtils {
    public static void show(Player player, TextColor color,int inMillis,int stayMillis,int outMillis){
        player.showTitle(Title.title(Component.text("\uF8BB").color(color),Component.empty(), Title.Times.times(Duration.ofMillis(inMillis),Duration.ofMillis(stayMillis),Duration.ofMillis(outMillis))));
    }
}
