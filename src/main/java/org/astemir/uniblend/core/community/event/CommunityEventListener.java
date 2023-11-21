package org.astemir.uniblend.core.community.event;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.astemir.uniblend.core.community.UTeam;
import org.astemir.uniblend.core.community.UniblendTeams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CommunityEventListener implements Listener {


    @EventHandler
    public void onChat(AsyncChatEvent e){
        Player player = e.getPlayer();
        if (UniblendTeams.INSTANCE.hasTeam(player)) {
            UTeam smplTeam = UniblendTeams.INSTANCE.getTeamOfPlayer(player.getName());
            e.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) -> Component.translatable("chat.type.text", sourceDisplayName.color(smplTeam.color()), message)));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        UniblendTeams.INSTANCE.updatePlayerTabName(player.getName());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }


}
