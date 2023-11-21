package org.astemir.uniblend.event;

import org.astemir.uniblend.core.particle.beta.BetaParticleEmitter;
import org.astemir.uniblend.core.particle.beta.UniblendBetaParticles;
import org.astemir.uniblend.core.particle.beta.UniblendBetaParticlesHandler;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UniblendEventListener implements Listener {

    private static Map<UUID, Vector> motions = new HashMap<>();
    public static Vector getMotion(Player player){
        if (motions.containsKey(player.getUniqueId())){
            return motions.get(player.getUniqueId());
        }
        return new Vector(0,0,0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e){
        if (e.hasChangedPosition()) {
            motions.put(e.getPlayer().getUniqueId(), e.getTo().clone().subtract(e.getFrom().clone()).toVector());
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e){}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if (motions.containsKey(player.getUniqueId())){
            motions.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHurt(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            PlayerHurtEvent hurtEvent = new PlayerHurtEvent((Player) e.getEntity(), e.getCause(), e.getDamage());
            Bukkit.getPluginManager().callEvent(hurtEvent);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent e){
        EntitySubmitDeathDropEvent dropEvent = new EntitySubmitDeathDropEvent(e.getEntity(),e.getDrops(),e.getDroppedExp());
        Bukkit.getPluginManager().callEvent(dropEvent);
        if (dropEvent.isCancelled()){
            e.setDroppedExp(dropEvent.getDroppedExp());
            e.getDrops().clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e){
        PlayerClickEvent clickEvent = new PlayerClickEvent(e.getPlayer(),e.getAction(),e.getItem(),e.getClickedBlock(),e.getBlockFace(),null,null,e.getHand());
        Bukkit.getPluginManager().callEvent(clickEvent);
        e.setCancelled(clickEvent.isCancelled());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractDirectlyWithEntity(PlayerInteractEntityEvent e){
        PlayerClickEvent clickEvent = new PlayerClickEvent(e.getPlayer(), Action.RIGHT_CLICK_AIR,e.getPlayer().getInventory().getItem(e.getHand()),e.getRightClicked().getLocation().getBlock(), BlockFace.DOWN,e.getRightClicked(),null,e.getHand());
        Bukkit.getPluginManager().callEvent(clickEvent);
        e.setCancelled(clickEvent.isCancelled());
    }
}
