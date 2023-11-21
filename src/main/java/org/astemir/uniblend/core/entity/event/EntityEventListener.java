package org.astemir.uniblend.core.entity.event;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import io.papermc.paper.event.player.PlayerUntrackEntityEvent;
import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.core.entity.UEntityHandler;
import org.astemir.uniblend.core.entity.UniblendEntities;
import org.astemir.uniblend.core.entity.parent.UEntity;
import org.astemir.uniblend.event.EntitySubmitDeathDropEvent;
import org.astemir.uniblend.utils.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

import java.util.ArrayList;
import java.util.List;


public class EntityEventListener implements Listener {


    @EventHandler
    public void onUnload(EntitiesUnloadEvent e) {
        List<Entity> livingEntities = new ArrayList<>();
        for (Entity entity : e.getEntities()) {
            if (entity instanceof LivingEntity) {
                livingEntities.add(entity);
            }
        }
        for (Entity entity : livingEntities) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (livingEntity == null || UEntityHandler.getEntity(livingEntity.getUniqueId()) == null) {
                continue;
            }
            UEntity activeMob = UEntityHandler.getEntity(livingEntity.getUniqueId());
            if (activeMob == null) {
                continue;
            }
            if (activeMob.isNaturalDespawn()) {
                activeMob.remove();
            }else{
                activeMob.save(activeMob.getHandle());
            }
        }
    }

    @EventHandler
    private void onLoad(EntitiesLoadEvent e){
        Chunk chunk = e.getChunk();
        if (chunk.isLoaded()) {
            Entity[] entityList = chunk.getEntities();
            if (entityList.length != 0) {
                for (Entity entity : entityList) {
                   UEntityHandler.loadFromEntity(entity);
                }
            }
        }
    }

    @EventHandler
    public void onEntityTracked(PlayerTrackEntityEvent e){
        UEntity entity = UEntityHandler.getEntity(e.getEntity().getUniqueId());
        if (entity != null) {
            Bukkit.getScheduler().runTaskLater(UniblendCorePlugin.getPlugin(),()->{
                entity.onTracked(e.getPlayer());
            },2);
        }
    }

    @EventHandler
    public void onEntityUntracked(PlayerUntrackEntityEvent e){
        UEntity entity = UEntityHandler.getEntity(e.getEntity().getUniqueId());
        if (entity != null) {
            entity.onUntracked(e.getPlayer());
        }
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent e){
        UEntity entity = UEntityHandler.getEntity(e.getEntity().getUniqueId());
        if (entity != null) {
            entity.onMove(e);
        }
    }

    @EventHandler
    public void onEntityAfterDeathDrop(EntitySubmitDeathDropEvent e){
        UEntity entity = UEntityHandler.getEntity(e.getEntity().getUniqueId());
        if (entity != null) {
            entity.onDrop(e);
            e.setCancelled(true);
        }
        UEntityHandler.INSTANCE.remove(entity);
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent e){
        UEntity entity = UEntityHandler.getEntity(e.getRightClicked().getUniqueId());
        if (entity != null) {
            entity.onClicked(e);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        UEntity entity = UEntityHandler.getEntity(e.getEntity().getUniqueId());
        if (entity != null) {
            e.setCancelled(true);
            if (!entity.onDeath(e)){
                e.getEntity().remove();
            }
        }
    }

    @EventHandler
    public void onEntityJump(EntityJumpEvent e) {
        UEntity entity = UEntityHandler.getEntity(e.getEntity().getUniqueId());
        if (entity != null) {
            entity.onJump(e);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof EnderDragon enderDragon){
            if (!NBTUtils.contains(e.getEntity(),"wounded")) {
                if (enderDragon.getHealth() < enderDragon.getMaxHealth() / 2) {
                    NBTUtils.set(e.getEntity(),"wounded",true);
                    if (UniblendEntities.INSTANCE.hasMatch("dragon_guardian")) {
                        UniblendEntities.spawn("dragon_guardian",enderDragon.getLocation());
                    }
                }
            }
        }
        UEntity entity = UEntityHandler.getEntity(e.getEntity().getUniqueId());
        if (entity != null) {
            entity.onHurt(e);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        UEntity entity = UEntityHandler.getEntity(e.getDamager().getUniqueId());
        if (entity != null) {
            entity.onDamageEntity(e);
        }
    }

}
