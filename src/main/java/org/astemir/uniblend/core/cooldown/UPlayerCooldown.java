package org.astemir.uniblend.core.cooldown;

import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class UPlayerCooldown {
    private UUID playerUUID;
    private String name;
    private int time = 0;

    public UPlayerCooldown(LivingEntity player, String name, int time) {
        this.playerUUID = player.getUniqueId();
        this.name = name;
        this.time = time;
    }

    public void update(){
        if (time > 0){
            time--;
        }
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }


    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}