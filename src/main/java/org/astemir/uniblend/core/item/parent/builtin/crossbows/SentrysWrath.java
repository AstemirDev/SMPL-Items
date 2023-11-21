package org.astemir.uniblend.core.item.parent.builtin.crossbows;

import org.astemir.uniblend.core.cooldown.UCooldownHandler;
import org.astemir.uniblend.core.entity.EntityTaskHandler;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.particle.UParticleBeam;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SentrysWrath extends UItem {

    @Property("arrow-pierce")
    private int arrowPierce = 120;
    @Property("cooldown")
    private int cooldown = 40;
    @Property("rays-count")
    private int raysCount = 5;
    @Property("damage")
    private int damage = 15;
    @Property("final-damage")
    private int finalDamage = 20;
    @Property("explosion-power")
    private int explosionPower = 10;
    @Property("velocity-multiplier")
    private double velocityMultiplier = 0.1;

    @Override
    public EventExecutionResult onShoot(EntityShootBowEvent e) {
        Entity projectile = e.getProjectile();
        if (projectile instanceof Arrow) {
            LivingEntity shooter = e.getEntity();
            ItemUtils.damageItem(shooter,e.getBow(),1);
            e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 1.5f);
            e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1, 0.5f);
            AbstractArrow arrow = EntityUtils.shootArrow(e.getEntity(), e.getBow(), 1, e.getForce(), 0, true);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            arrow.setGravity(false);
            arrow.setPierceLevel(arrowPierce);
            arrow.setVelocity(arrow.getVelocity().clone().multiply(velocityMultiplier));
            PacketUtils.sendPacketHideEntity(arrow);
            EntityTaskHandler.INSTANCE.add(new EntityTaskHandler.EntityRunnable(arrow) {
                @Override
                public void runOut(Entity entity, long ticks) {
                    for (int j = 0;j<3;j++) {
                        arrow.getWorld().playSound(arrow.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, 2);
                        Location randomLoc = arrow.getLocation().clone().add(new Vector(RandomUtils.randomFloat(-3, 3), RandomUtils.randomFloat(2, 4), RandomUtils.randomFloat(-3, 3)));
                        Vector dir = arrow.getLocation().toVector().add(randomLoc.toVector().multiply(-1)).normalize();
                        for (int i = 0; i < raysCount; i++) {
                            UParticleBeam.sendParticleLightning(Particle.SOUL_FIRE_FLAME, randomLoc, dir, 2, (ent) -> {
                                if (!ent.getUniqueId().equals(shooter.getUniqueId())) {
                                    ent.damage(finalDamage, shooter);
                                    if (UCooldownHandler.doWithCooldown(shooter, "sentrys_wrath",cooldown)) {
                                        EntityUtils.explodeFirework(ent.getLocation(), explosionPower/4, FireworkEffect.builder().withFlicker().withColor(Color.AQUA).build());
                                    }
                                    return true;
                                } else {
                                    return false;
                                }
                            }, 6, 0.5f, false);
                            arrow.getLocation().getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, arrow.getLocation().clone().add(RandomUtils.randomFloat(-2f, 2f), RandomUtils.randomFloat(-2f, 2f), RandomUtils.randomFloat(-2f, 2f)), 0, 0, 0, 0);
                            arrow.getLocation().getWorld().spawnParticle(Particle.REDSTONE, arrow.getLocation().clone().add(RandomUtils.randomFloat(-0.5f, 0.5f), RandomUtils.randomFloat(-0.5f, 0.5f), RandomUtils.randomFloat(-0.5f, 0.5f)), 10, 0, 0, 0, new Particle.DustOptions(Color.AQUA, 2f));
                        }
                    }
                    arrow.getWorld().playSound(arrow.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, 0.5f);
                    arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 2, 1f);
                    EntityUtils.explodeFirework(entity.getLocation(), explosionPower/2, FireworkEffect.builder().withFlicker().withColor(Color.AQUA).withColor(Color.BLUE).build());
                }

                @Override
                public void run(Entity entity, long ticks) {
                    if (ticks % 5 == 0 && RandomUtils.doWithChance(50)) {
                        if (arrow.isInBlock()){
                            cancel();
                            arrow.remove();
                        }else{
                            arrow.getWorld().playSound(arrow.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, 2);
                            Location randomLoc = arrow.getLocation().clone().add(new Vector(RandomUtils.randomFloat(-3,3), RandomUtils.randomFloat(2, 4), RandomUtils.randomFloat(-3, 3)));
                            Vector dir = arrow.getLocation().toVector().add(randomLoc.toVector().multiply(-1)).normalize();
                            for (int i = 0;i<raysCount;i++) {
                                UParticleBeam.sendParticleLightning(Particle.SOUL_FIRE_FLAME,randomLoc , dir, 2, (ent) -> {
                                    if (!ent.getUniqueId().equals(shooter.getUniqueId())) {
                                        ent.damage(damage, shooter);
                                        if (UCooldownHandler.doWithCooldown(shooter,"sentrys_wrath",cooldown)) {
                                            EntityUtils.explodeFirework(ent.getLocation(), explosionPower, FireworkEffect.builder().withFlicker().withColor(Color.AQUA).build());
                                        }
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }, 6, 0.5f, false).getLoc();
                            }
                        }
                    }
                    arrow.getLocation().getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, arrow.getLocation().clone().add(RandomUtils.randomFloat(-2f, 2f), RandomUtils.randomFloat(-2f, 2f), RandomUtils.randomFloat(-2f, 2f)), 0, 0, 0, 0);
                    arrow.getLocation().getWorld().spawnParticle(Particle.REDSTONE, arrow.getLocation().clone().add(RandomUtils.randomFloat(-0.5f, 0.5f), RandomUtils.randomFloat(-0.5f,0.5f), RandomUtils.randomFloat(-0.5f, 0.5f)), 10, 0, 0, 0, new Particle.DustOptions(Color.AQUA,2f));

                }
            }).lifespan(100);
            return EventExecutionResult.CANCEL;
        }
        return super.onShoot(e);
    }


    @Override
    public EventExecutionResult onTick(Player player,ItemStack itemStack,long ticks) {
        if (ticks % 10 == 0){
            Location loc = player.getLocation();
            for (int i = 0;i<10;i++) {
                loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(RandomUtils.randomFloat(-1f, 1f), 1.75f+RandomUtils.randomFloat(-0.5f,0.1f), RandomUtils.randomFloat(-1f, 1f)), 0, 0, 0, 0, new Particle.DustOptions(Color.AQUA,1f));
            }
        }
        return super.onTick(player,itemStack,ticks);
    }
}
