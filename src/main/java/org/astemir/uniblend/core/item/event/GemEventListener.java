package org.astemir.uniblend.core.item.event;

import org.astemir.uniblend.core.cooldown.UCooldownHandler;
import org.astemir.uniblend.core.cooldown.UPlayerCooldown;
import org.astemir.uniblend.core.item.parent.UItemSocketGem;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.event.EntitySubmitDeathDropEvent;
import org.astemir.uniblend.misc.Pair;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Map;

public class GemEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onDropMob(EntitySubmitDeathDropEvent e){
        if (e.getEntity().getKiller() != null) {
            ItemStack itemStack = EntityUtils.getItemInMainHand(e.getEntity().getKiller());
            if (itemStack != null) {
                Pair<UItemSocketGem, Integer> pair = UItemSocketGem.getItemGem(itemStack);
                if (pair != null) {
                    UItemSocketGem gem = pair.getKey();
                    int level = pair.getValue();
                    int chanceToLose = 100;
                    for (UItemSocketGem.GemAttribute gemAttribute : gem.getGemAttributes()) {
                        if (gemAttribute.getType() == UItemSocketGem.GemAttributeType.MISFORTUNE_CHANCE) {
                            chanceToLose = (int) gem.applyToNumber(gemAttribute, level, chanceToLose);
                        }
                    }
                    if (RandomUtils.doWithChance(chanceToLose - 100)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrop(BlockDropItemEvent e){
        ItemStack itemStack = EntityUtils.getItemInMainHand(e.getPlayer());
        if (itemStack != null) {
            Pair<UItemSocketGem, Integer> pair = UItemSocketGem.getItemGem(itemStack);
            if (pair != null) {
                UItemSocketGem gem = pair.getKey();
                int level = pair.getValue();
                int chanceToLose = 100;
                for (UItemSocketGem.GemAttribute gemAttribute : gem.getGemAttributes()) {
                    if (gemAttribute.getType() == UItemSocketGem.GemAttributeType.MISFORTUNE_CHANCE) {
                        chanceToLose = (int) gem.applyToNumber(gemAttribute, level, chanceToLose);
                    }
                }
                if (RandomUtils.doWithChance(chanceToLose - 100)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof LivingEntity livingEntity){
            ItemStack itemStack = EntityUtils.getItemInMainHand(livingEntity);
            if (itemStack != null) {
                Pair<UItemSocketGem, Integer> pair = UItemSocketGem.getItemGem(itemStack);
                if (pair != null) {
                    UItemSocketGem gem = pair.getKey();
                    int level = pair.getValue();
                    int chanceToHit = 100;
                    for (UItemSocketGem.GemAttribute gemAttribute : gem.getGemAttributes()) {
                        if (gemAttribute.getType() == UItemSocketGem.GemAttributeType.MELEE_DAMAGE) {
                            e.setDamage(gem.applyToNumber(gemAttribute, level, e.getDamage()));
                        }
                        if (gemAttribute.getType() == UItemSocketGem.GemAttributeType.MISS_CHANCE) {
                            chanceToHit = (int) gem.applyToNumber(gemAttribute, level, chanceToHit);
                        }
                    }
                    if (RandomUtils.doWithChance(chanceToHit - 100)) {
                        e.setCancelled(true);
                    }
                }
            }
        }else
        if (e.getDamager() instanceof Projectile projectile){
            if (projectile.getShooter() != null && projectile.getShooter() instanceof LivingEntity livingEntity){
                ItemStack itemStack = EntityUtils.getItemInMainHand(livingEntity);
                if (itemStack != null) {
                    Pair<UItemSocketGem, Integer> pair = UItemSocketGem.getItemGem(itemStack);
                    if (pair != null) {
                        UItemSocketGem gem = pair.getKey();
                        int level = pair.getValue();
                        int chanceToHit = 100;
                        for (UItemSocketGem.GemAttribute gemAttribute : gem.getGemAttributes()) {
                            if (gemAttribute.getType() == UItemSocketGem.GemAttributeType.RANGED_DAMAGE) {
                                e.setDamage(gem.applyToNumber(gemAttribute, level, e.getDamage()));
                            }
                        }
                        if (RandomUtils.doWithChance(chanceToHit - 100)) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onSprint(PlayerToggleSprintEvent e){
        Player player = e.getPlayer();
        if (e.isSprinting()) {
            items:
            for (Map.Entry<EquipmentSlot, ItemStack> entry : EntityUtils.getEquipmentArmor(player).entrySet()) {
                if (entry.getValue() != null) {
                    Pair<UItemSocketGem, Integer> pair = UItemSocketGem.getItemGem(entry.getValue());
                    if (pair != null) {
                        UItemSocketGem gem = pair.getKey();
                        for (UItemSocketGem.GemAttribute gemAttribute : gem.getGemAttributes()) {
                            if (gemAttribute.getType() == UItemSocketGem.GemAttributeType.DASH) {
                                if (UCooldownHandler.doWithCooldown(player, "dash", 20)) {
                                    UParticleEffect effect = new UParticleEffect(Particle.CLOUD).size(1, 1, 1).speed(1, 1, 1).count(20);
                                    Vector velocity = EntityUtils.getEntityDirection(player).multiply(gem.applyToNumber(gemAttribute,pair.getValue(),0));
                                    velocity.setY(0.25f);
                                    for (int i = 0;i<10;i++) player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SAND_PLACE,1,RandomUtils.randomFloat(0.9f,1.5f));
                                    player.setVelocity(velocity);
                                    effect.play(player.getLocation());
                                    e.setCancelled(true);
                                    break items;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onProjectileDamage(ProjectileHitEvent e){
        if (e.getEntity().getShooter() != null && e.getEntity().getShooter() instanceof LivingEntity livingEntity){
            ItemStack itemStack = EntityUtils.getItemInMainHand(livingEntity);
            if (itemStack != null) {
                Pair<UItemSocketGem, Integer> pair = UItemSocketGem.getItemGem(itemStack);
                if (pair != null) {
                    UItemSocketGem gem = pair.getKey();
                    int level = pair.getValue();
                    int chanceToHit = 100;
                    for (UItemSocketGem.GemAttribute gemAttribute : gem.getGemAttributes()) {
                        if (gemAttribute.getType() == UItemSocketGem.GemAttributeType.MISS_CHANCE) {
                            chanceToHit = (int) gem.applyToNumber(gemAttribute, level, chanceToHit);
                        }
                    }
                    if (RandomUtils.doWithChance(chanceToHit - 100)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
