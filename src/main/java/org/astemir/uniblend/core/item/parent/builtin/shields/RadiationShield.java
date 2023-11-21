package org.astemir.uniblend.core.item.parent.builtin.shields;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RadiationShield extends UItem {

    @Property("effect")
    private PotionEffect effect = new PotionEffect(PotionEffectType.POISON, 150, 1, false, false);
    @Override
    public EventExecutionResult onHurtByEntity(EntityDamageByEntityEvent e) {
        Player player = (Player)e.getEntity();
        if (player.isBlocking()){
            if (e.getDamager() instanceof LivingEntity){
                if (!((LivingEntity)e.getDamager()).hasPotionEffect(effect.getType())) {
                    ((LivingEntity) e.getDamager()).addPotionEffect(effect);
                }
            }
        }
        return super.onHurtByEntity(e);
    }

    @Override
    public EventExecutionResult onTick(Player player,ItemStack itemStack,long ticks) {
        if (ticks % 10 == 0){
            Location loc = player.getLocation();
            for (int i = 0;i<5;i++) {
                loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(RandomUtils.randomFloat(-1f, 1f), 1.75f+RandomUtils.randomFloat(-1.5f,-0.25f), RandomUtils.randomFloat(-1f, 1f)), 0, 0, 0, 0,new Particle.DustOptions(Color.GREEN,2f));
            }
        }
        return super.onTick(player,itemStack,ticks);
    }
}
