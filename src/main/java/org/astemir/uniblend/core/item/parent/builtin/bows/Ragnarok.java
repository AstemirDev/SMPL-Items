package org.astemir.uniblend.core.item.parent.builtin.bows;

import org.astemir.uniblend.core.cooldown.UCooldownHandler;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.core.item.parent.UItemBow;
import org.astemir.uniblend.core.particle.UParticleBeam;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.*;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Ragnarok extends UItemBow {

    @Property("segments-count")
    private int segments = 10;
    @Property("lightning-chance")
    private int lightningChance = 50;
    @Property("cooldown")
    private int cooldown = 200;
    @Property("damage")
    private double damage = 5.0;
    @Override
    public boolean shoot(LivingEntity shooter, ItemStack bow, EquipmentSlot hand, float force) {
        UParticleEffect effect = new UParticleEffect(Particle.SOUL_FIRE_FLAME).size(1,1,1).speed(1,1,1).randomSpeed().renderTimes(10);
        effect.play(shooter.getLocation());
        shooter.getWorld().playSound(shooter.getLocation(), Sound.ENTITY_BLAZE_SHOOT,0.5f,1.5f);
        shooter.getWorld().playSound(shooter.getLocation(), Sound.ENTITY_SKELETON_SHOOT,1,1.5f*force);
        shooter.getWorld().playSound(shooter.getLocation(), Sound.ITEM_CROSSBOW_SHOOT,1,1.5f*force);
        int segments = (int) (this.segments*force);
        ItemUtils.damageItem(shooter,bow,1);
        for (int i = 0;i<=segments/3;i++) {
            UParticleBeam.sendParticleLightning(Particle.END_ROD, EntityUtils.getEntityEyeLocation(shooter), EntityUtils.getEntityDirection(shooter), 2, (ent) -> {
                if (!ent.getUniqueId().equals(shooter.getUniqueId())) {
                    ent.damage(damage, shooter);
                    if (RandomUtils.doWithChance(lightningChance) && UCooldownHandler.doWithCooldown(shooter,getNameKey(),cooldown)) {
                        ent.getWorld().strikeLightning(ent.getLocation());
                    }
                    return true;
                } else {
                    return false;
                }
            }, segments, 0.2f,false);
        }
        return false;
    }

}
