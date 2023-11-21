package org.astemir.uniblend.core.item.parent.builtin.bows;

import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.item.parent.UItemBow;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.core.particle.UParticleBeam;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.misc.SoundInstance;
import org.astemir.uniblend.misc.ValueRange;
import org.astemir.uniblend.utils.PlayerUtils;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CursedWatcher extends UItemBow {

    @Property("particle")
    private UParticleEffect particle = new UParticleEffect(Particle.REDSTONE).color(Color.fromRGB(0,255,0),0.75f);
    @Property("sound")
    private SoundInstance sound;
    @Property("segments-count")
    private ValueRange segments = new ValueRange(10,20);
    @Property("damage")
    private double damage = 7.25;
    @Override
    public boolean shoot(LivingEntity shooter, ItemStack bow, EquipmentSlot hand, float force) {
        sound.play(shooter.getLocation(),0.5f,1.5f*force);
        shooter.getWorld().playSound(shooter.getLocation(), Sound.ENTITY_BLAZE_SHOOT,0.5f,1.5f);
        shooter.getWorld().playSound(shooter.getLocation(), Sound.ENTITY_SKELETON_SHOOT,1,1.5f*force);
        int segments = (int) (this.segments.get()*force);
        ItemUtils.damageItem(shooter,bow,1);
        shooter.setVelocity(EntityUtils.getEntityDirection(shooter).multiply(0.5f+force));
        shooter.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,20,0,true,true));
        for (int i = 0;i<segments;i++) {
            UParticleBeam.sendParticleLightning(particle, EntityUtils.getEntityEyeLocation(shooter), EntityUtils.getEntityDirection(shooter), 1, (ent) -> {
                if (!ent.getUniqueId().equals(shooter.getUniqueId())) {
                    ent.damage(damage, shooter);
                    return true;
                } else {
                    return false;
                }
            }, segments, 1, false);
        }
        return false;
    }

}
