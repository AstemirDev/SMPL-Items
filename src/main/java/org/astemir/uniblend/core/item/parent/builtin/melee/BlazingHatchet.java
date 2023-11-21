package org.astemir.uniblend.core.item.parent.builtin.melee;

import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.core.cooldown.UCooldownHandler;
import org.astemir.uniblend.core.item.parent.UItemSword;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Vector3d;

public class BlazingHatchet extends UItemSword {
    @Property("ability-velocity")
    private Vector3d velocity = new Vector3d(2,1.25f,2);
    @Property("ability-radius")
    private Vector3d radius = new Vector3d(1.5f,1,1.5f);
    @Property("ability-cooldown")
    private int cooldown = 80;
    @Property("ability-duration")
    private int duration = 150;
    @Override
    public EventExecutionResult onAttackEntity(EntityDamageByEntityEvent e) {
        LivingEntity damager = (LivingEntity) e.getDamager();
        boolean blocking = false;
        if (e.getEntity() instanceof Player){
            blocking = ((Player)e.getEntity()).isBlocking();
        }
        if (UCooldownHandler.doWithCooldown(damager, "blazing",cooldown)) {
            if (!blocking) {
                Location loc = e.getEntity().getLocation();
                loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1, 0.5f);
                for (int i = 0; i < 2; i++) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 0.5f);
                    loc.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(0, 0.5f, 0), 100, 0.5f, 0.5f, 0.5f, 0.1f);
                }
                loc.getNearbyLivingEntities(radius.x,radius.y,radius.z).forEach((entity) -> {
                    if (!entity.getUniqueId().equals(damager.getUniqueId())) {
                        blaze(damager, entity);
                    }
                });
            }
        }
        return super.onAttackEntity(e);
    }

    private void blaze(LivingEntity damager,Entity entity){
        if (!entity.getUniqueId().equals(damager.getUniqueId())) {
            Vector direction = EntityUtils.getEntityDirection(damager).clone().normalize();
            entity.setFireTicks(duration);
            new BukkitRunnable(){
                @Override
                public void run() {
                    entity.setVelocity(new Vector(direction.getX()*velocity.x,velocity.y,direction.getZ()*velocity.z));
                }
            }.runTaskLater(UniblendCorePlugin.getPlugin(),1);
        }
    }

    @Override
    public EventExecutionResult onBreakBlock(BlockBreakEvent e) {
        if (ItemUtils.isWoodLog(e.getBlock().getType())){
            e.setDropItems(false);
            e.getBlock().getWorld().spawnParticle(Particle.FLAME, e.getBlock().getLocation().toCenterLocation(), 10, 0.25f, 0.25f, 0.25f, 0.1f);
            e.getBlock().getWorld().dropItem(e.getBlock().getLocation(),new ItemStack(Material.CHARCOAL));
        }
        return super.onBreakBlock(e);
    }
}
