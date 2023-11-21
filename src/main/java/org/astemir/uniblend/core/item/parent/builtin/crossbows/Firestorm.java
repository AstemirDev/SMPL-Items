package org.astemir.uniblend.core.item.parent.builtin.crossbows;

import org.astemir.uniblend.core.item.parent.UItemBow;
import org.astemir.uniblend.core.entity.EntityTaskHandler;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Firestorm extends UItemBow {
    @Property("explosion-power")
    private int explosionPower = 20;
    @Override
    public boolean shoot(LivingEntity shooter, ItemStack bow, EquipmentSlot hand, float force) {
        ItemUtils.damageItem(shooter,bow,1);
        shooter.getWorld().playSound(shooter.getLocation(),Sound.ITEM_CROSSBOW_SHOOT,1,1.5f);
        shooter.getWorld().playSound(shooter.getLocation(),Sound.ITEM_CROSSBOW_SHOOT,1,0.5f);
        return false;
    }

    @Override
    public Entity createProjectile(LivingEntity shooter,ItemStack bowItem,ItemStack arrowItem,float force,Entity arrowProjectile) {
        Entity projectile = super.createProjectile(shooter,bowItem,arrowItem,force,arrowProjectile);
        if (projectile instanceof AbstractArrow){
            AbstractArrow arrow = EntityUtils.shootArrow(shooter,bowItem,1,force,0,true);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            EntityTaskHandler.INSTANCE.add(new EntityTaskHandler.EntityRunnable(arrow){
                @Override
                public void run(Entity entity, long ticks) {
                    entity.getWorld().spawnParticle(Particle.FLAME, entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), 0, 0, 0, 0);
                    if (arrow.isInBlock()){
                        FireworkEffect effect = FireworkEffect.builder().withColor(Color.ORANGE,Color.YELLOW,Color.RED).withFade(Color.ORANGE,Color.YELLOW,Color.RED).with(FireworkEffect.Type.BALL_LARGE).build();
                        FireworkEffect effect1 = FireworkEffect.builder().withColor(Color.ORANGE,Color.YELLOW,Color.RED).withFade(Color.ORANGE,Color.YELLOW,Color.RED).with(FireworkEffect.Type.BURST).build();
                        EntityUtils.explodeFirework(arrow.getLocation(), explosionPower,effect,effect,effect,effect1,effect1);
                        entity.getWorld().playSound(arrow.getLocation(),Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,2,0.5f);
                        entity.getWorld().playSound(arrow.getLocation(),Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE,2,0.5f);
                        arrow.remove();
                    }
                }

            });
        }
        return super.createProjectile(shooter,bowItem,arrowItem,force,arrowProjectile);
    }

    @Override
    public boolean projectileDamageEntity(Entity projectile, Entity damaged) {
        FireworkEffect effect = FireworkEffect.builder().withColor(Color.ORANGE,Color.YELLOW,Color.RED).withFade(Color.ORANGE,Color.YELLOW,Color.RED).with(FireworkEffect.Type.BALL_LARGE).build();
        FireworkEffect effect1 = FireworkEffect.builder().withColor(Color.ORANGE,Color.YELLOW,Color.RED).withFade(Color.ORANGE,Color.YELLOW,Color.RED).with(FireworkEffect.Type.BURST).build();
        projectile.getWorld().playSound(projectile.getLocation(),Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,2,0.5f);
        projectile.getWorld().playSound(projectile.getLocation(),Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE,2,0.5f);
        EntityUtils.explodeFirework(projectile.getLocation(), explosionPower,effect,effect,effect,effect1,effect1);
        return true;
    }
}
