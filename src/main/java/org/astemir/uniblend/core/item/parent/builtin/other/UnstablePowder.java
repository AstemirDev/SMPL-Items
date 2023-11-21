package org.astemir.uniblend.core.item.parent.builtin.other;

import org.astemir.uniblend.core.cooldown.UCooldownHandler;
import org.astemir.uniblend.event.*;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.joml.Vector3d;

public class UnstablePowder extends UItem {
    @Property("self-effect")
    private PotionEffect selfEffect = new PotionEffect(PotionEffectType.GLOWING,400,0,false,false);
    @Property("cooldown")
    private int cooldown = 200;
    @Property("radius")
    private Vector3d radius = new Vector3d(10,10,10);

    @Override
    public EventExecutionResult onRightClick(PlayerClickEvent e) {
        if (UCooldownHandler.doWithCooldown(e.getPlayer(),"unstable_powder",cooldown)) {
            if (e.getHand() == EquipmentSlot.HAND) {
                e.getPlayer().swingMainHand();
            }else{
                e.getPlayer().swingOffHand();
            }
            e.getItem().subtract(1);
            UParticleEffect effect = new UParticleEffect(Particle.END_ROD).speed(0.5f,0.5f,0.5f).randomSpeed().renderTimes(30);
            effect.play(e.getPlayer().getLocation());
            e.getPlayer().getLocation().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_WITCH_DRINK,1,0.5f);
            e.getPlayer().getLocation().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE,1,2);
            e.getPlayer().getNearbyEntities(radius.x,radius.y,radius.z).forEach((entity)->{
                if (entity instanceof LivingEntity){
                    ((LivingEntity)entity).addPotionEffect(selfEffect);
                }
            });
        }
        return super.onRightClick(e);
    }
}
