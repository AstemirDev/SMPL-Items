package org.astemir.uniblend.core.cooldown;

import org.astemir.uniblend.core.UniblendRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class UCooldownHandler extends UniblendRegistry.Concurrent<UPlayerCooldown> {

    public static UCooldownHandler INSTANCE;
    public UCooldownHandler() {
        INSTANCE = this;
    }
    @Override
    public void onUpdate(long tick) {
        for (UPlayerCooldown cooldown : getEntries()) {
            if (cooldown.getTime() <= 0){
                remove(cooldown);
            }else{
                cooldown.update();
            }
        }
    }

    public void setCooldown(LivingEntity player, String name, int time){
        if (time > 0) {
            UPlayerCooldown cooldown = getCooldown(player, name);
            if (cooldown == null) {
                add(new UPlayerCooldown(player, name, time));
            } else {
                remove(cooldown);
                cooldown.setTime(time);
                add(cooldown);
            }
        }
    }

    public boolean hasCooldown(LivingEntity player,String name){
        if (getCooldown(player,name) == null){
            return false;
        }
        return true;
    }

    public int getCooldownInTicks(LivingEntity player,String name){
        UPlayerCooldown cooldown = getCooldown(player,name);
        if (cooldown != null){
            return cooldown.getTime();
        }
        return 0;
    }


    private UPlayerCooldown getCooldown(LivingEntity player, String name){
        for (UPlayerCooldown cooldown : getEntries()) {
            if (cooldown.getPlayerUUID().equals(player.getUniqueId())){
                if (cooldown.getName().equals(name)){
                    return cooldown;
                }
            }
        }
        return null;
    }

    public static boolean doWithCooldown(Entity entity, String name, int ticks){
        if (entity instanceof LivingEntity livingEntity) {
            if (!UCooldownHandler.INSTANCE.hasCooldown(livingEntity, name)) {
                setCooldown(entity,name,ticks);
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }

    public static void setCooldown(Entity entity,String name,int ticks){
        UCooldownHandler.INSTANCE.setCooldown((LivingEntity) entity, name, ticks);
    }
}
