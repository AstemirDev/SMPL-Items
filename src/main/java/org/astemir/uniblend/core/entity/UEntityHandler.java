package org.astemir.uniblend.core.entity;


import com.ticxo.modelengine.api.ModelEngineAPI;
import org.astemir.uniblend.core.UniblendRegistry;
import org.astemir.uniblend.core.entity.parent.builtin.DragonGuardian;
import org.astemir.uniblend.core.entity.parent.builtin.Hoglibor;
import org.astemir.uniblend.core.entity.event.EntityEventListener;
import org.astemir.uniblend.core.entity.parent.UEntity;
import org.astemir.uniblend.core.entity.parent.UMob;
import org.astemir.uniblend.utils.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UEntityHandler extends UniblendRegistry.Concurrent<UEntity>{

    public static UEntityHandler INSTANCE;
    public UEntityHandler() {
        INSTANCE = this;
    }

    @Override
    public void onRegister() {
        registerEvent(new EntityEventListener());
    }

    @Override
    public void onEnable() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                loadFromEntity(entity);
            }
        }
    }

    @Override
    public void onDisable() {
        for (UEntity entry : getEntries()) {
            entry.untrackForce();
        }
    }

    @Override
    public void onSetupLookups() {
        setLookup("entity", UEntity.class);
        setLookup("mob", UMob.class);
        setLookup("hoglibor", Hoglibor.class);
        setLookup("dragon_guardian", DragonGuardian.class);
    }

    @Override
    public void onUpdate(long tick) {
        for (UEntity entry : getEntries()) {
            entry.update(tick);
        }
    }

    @Override
    public void clear() {
        for (UEntity entry : getEntries()) {
            entry.remove();
        }
    }


    public static UEntity getEntity(UUID uuid){
        for (UEntity entry : INSTANCE.getEntries()) {
            if (entry.getHandle() != null) {
                if (entry.getHandle().getUniqueId() == uuid){
                    return entry;
                }
            }
        }
        return null;
    }

    public static void loadFromEntity(Entity entity){
        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
            if (NBTUtils.contains(entity,"id")) {
                if (NBTUtils.get(entity,"despawn",Boolean.class)){
                    entity.remove();
                } else {
                    UniblendEntities.spawn(NBTUtils.get(entity,"id",String.class),entity.getLocation());
                    if (ModelEngineAPI.isModeledEntity(entity.getUniqueId())) {
                        ModelEngineAPI.removeModeledEntity(entity.getUniqueId());
                    }
                    entity.remove();
                }
            }
        }
    }
}
