package org.astemir.uniblend.core.item.parent.builtin.bows;

import org.astemir.uniblend.core.entity.EntityTaskHandler;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.item.parent.UItemBow;
import org.astemir.uniblend.core.particle.UParticleBeam;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.*;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Trailblazer extends UItemBow {
    @Property("explosion-power")
    private int power = 20;
    @Property("rays-count")
    private int raysCount = 6;
    @Property("damage")
    private double damage = 5.0;
    @Property("fire-ticks")
    private int fireTicks = 100;
    @Override
    public Entity createProjectile(LivingEntity shooter, ItemStack bowItem, ItemStack arrowItem, float force, Entity arrowProjectile) {
        Entity arrow = super.createProjectile(shooter,bowItem,arrowItem,force,arrowProjectile);
        arrow.setFireTicks(99999);
        EntityTaskHandler.INSTANCE.add(new EntityTaskHandler.EntityRunnable(arrow){
            @Override
            public void run(Entity entity, long ticks) {
                if (ticks % 2 == 0) {
                    entity.getWorld().spawnParticle(Particle.FLAME, entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), 0, 0, 0, 0);
                }
            }
        }).lifespan(100);
        return arrow;
    }

    @Override
    public boolean shoot(LivingEntity player, ItemStack bow, EquipmentSlot hand, float force) {
        UParticleEffect effect = new UParticleEffect(Particle.FLAME).size(1,1,1).speed(1,1,1).randomSpeed().renderTimes(10);
        effect.play(player.getLocation());
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT,0.5f,1.5f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SKELETON_SHOOT,1,1.5f*force);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL,1,1.5f*force);
        return true;
    }

    @Override
    public boolean projectileDamageEntity(Entity projectile, Entity damaged) {
        FireworkEffect effect = FireworkEffect.builder().withColor(Color.ORANGE,Color.YELLOW,Color.RED).withFade(Color.ORANGE,Color.YELLOW,Color.RED).with(FireworkEffect.Type.BALL_LARGE).build();
        projectile.getWorld().playSound(projectile.getLocation(),Sound.ENTITY_WITHER_SHOOT,2,0.5f);
        projectile.getWorld().playSound(projectile.getLocation(),Sound.ENTITY_BLAZE_SHOOT,2,0.5f);
        EntityUtils.explodeFirework(projectile.getLocation(), power,effect);
        Entity shooter = (Entity) ((AbstractArrow) projectile).getShooter();
        for (int i = 0;i<raysCount;i++) {
            UParticleBeam.sendParticleLightning(new UParticleEffect(Particle.FLAME), projectile.getLocation(), new Vector(Math.cos(RandomUtils.randomInt(0, 360)), 1, Math.sin(RandomUtils.randomInt(0, 360))), 1, (entity) -> {
                if (!entity.getUniqueId().equals(shooter.getUniqueId())) {
                    entity.damage(damage, shooter);
                    entity.setFireTicks(fireTicks);
                    return true;
                }
                return false;
            }, 4, 0.5f, false);
        }
        return true;
    }

}
