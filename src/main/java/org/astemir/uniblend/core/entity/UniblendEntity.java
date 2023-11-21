package org.astemir.uniblend.core.entity;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.astemir.uniblend.event.EntitySubmitDeathDropEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public interface UniblendEntity {

    default void onSetup(){}
    default void onUpdateAlive(long globalTicks){}
    default boolean onDeath(EntityDeathEvent e){return false;}
    default void onHurt(EntityDamageEvent e){}
    default void onDamageEntity(EntityDamageEvent e){}
    default void onClicked(PlayerInteractEntityEvent e){}
    default void onJump(EntityJumpEvent e){}
    default void onMove(EntityMoveEvent e) {}
    default void onTracked(Player player) {}
    default void onUntracked(Player player) {}
    default void onDrop(EntitySubmitDeathDropEvent e){}
}
