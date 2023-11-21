package org.astemir.uniblend.core.item.parent.builtin.melee;

import org.astemir.uniblend.core.cooldown.UCooldownHandler;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.entity.utils.MetadataUtils;
import org.astemir.uniblend.core.item.parent.UItemSword;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.core.projectile.UProjectile;
import org.astemir.uniblend.core.projectile.builtin.BloodPact;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.event.PlayerClickEvent;
import org.astemir.uniblend.core.particle.UParticleBeam;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.misc.SoundInstance;
import org.astemir.uniblend.utils.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BloodMourner extends UItemSword {

    @Property("attack-sound")
    private SoundInstance soundInstance = new SoundInstance("uniblend.items.bloodmourner",1,1);
    @Property("sweep-sound")
    private SoundInstance sweepSound = new SoundInstance(Sound.ENTITY_PLAYER_ATTACK_SWEEP,1,1);
    @Property("projectile")
    private UProjectile projectile;
    @Property("cooldown-first")
    private int firstAttackCooldown = 5;
    @Property("cooldown-second")
    private int secondAttackCooldown = 80;

    @Override
    public EventExecutionResult onLeftClick(PlayerClickEvent e) {
        if (UCooldownHandler.doWithCooldown(e.getPlayer(),"bloodmourner",firstAttackCooldown)) {
            sweepSound.play(e.getPlayer().getLocation(), 1f, RandomUtils.randomFloat(1.5f, 1.75f));
            soundInstance.play(e.getPlayer().getLocation(), 0.5f, RandomUtils.randomFloat(0.9f, 1.2f));
            BloodPact bloodPact = projectile.create();
            bloodPact.shoot(e.getPlayer(), EntityUtils.getEntityEyeLocation(e.getPlayer()));
            bloodPact.setVelocity(EntityUtils.getEntityDirection(e.getPlayer()).multiply(3));
        }
        return super.onLeftClick(e);
    }

    @Override
    public EventExecutionResult onRightClick(PlayerClickEvent e) {
        if (UCooldownHandler.doWithCooldown(e.getPlayer(),"bloodmourner2",secondAttackCooldown)) {
            sweepSound.play(e.getPlayer().getLocation(), 1f, RandomUtils.randomFloat(1.5f, 1.75f));
            soundInstance.play(e.getPlayer().getLocation(), 0.5f, RandomUtils.randomFloat(0.9f, 1.2f));
            Location location = EntityUtils.getEntityEyeLocation(e.getPlayer());
            for (int i = 0;i<4;i++) {
                BloodPact bloodPact = projectile.create();
                bloodPact.shoot(e.getPlayer(),location.clone().add(Math.cos(i)*2f,0.5f,Math.sin(i)*2f));
                bloodPact.setVelocity(EntityUtils.getEntityDirection(e.getPlayer()).multiply(3));
            }
            e.getPlayer().swingHand(e.getHand());
            e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(-1f));
        }
        return super.onRightClick(e);
    }


    @Override
    public EventExecutionResult onHitByProjectile(ProjectileHitEvent e) {
        reflect(e.getHitEntity(),e.getEntity());
        return EventExecutionResult.CANCEL;
    }

    @Override
    public EventExecutionResult onTick(Player player,ItemStack itemStack,long ticks) {
        if (ticks % 5 == 0) {
            for (Entity nearbyEntity : player.getNearbyEntities(3, 3, 3)) {
                if (nearbyEntity instanceof LivingEntity) {
                    if (!nearbyEntity.getUniqueId().equals(player.getUniqueId())) {
                        lightning(player, nearbyEntity);
                    }
                }
            }
        }
        return super.onTick(player,itemStack,ticks);
    }

    public void lightning(Entity owner,Entity entity){
        Location loc = owner.getLocation().clone().add(0, owner.getHeight() / 2, 0);
        Location livingLoc = entity.getLocation().clone().add(0, entity.getHeight(), 0);
        Vector direction = livingLoc.clone().add(RandomUtils.randomFloat(-0.1f, 0.1f), RandomUtils.randomFloat(-0.25f, 0.25f), RandomUtils.randomFloat(-0.1f, 0.1f)).subtract(loc.clone().add(RandomUtils.randomFloat(-0.1f, 0.1f), RandomUtils.randomFloat(-0.25f, 0.25f), RandomUtils.randomFloat(-0.1f, 0.1f))).toVector();
        UParticleBeam.sendObservableParticleLightning(new UParticleEffect(Particle.FALLING_DUST).block(Material.REDSTONE_BLOCK), loc, direction, (int) (loc.distance(livingLoc) / 3), (target) -> {
            if (!target.getUniqueId().equals(owner.getUniqueId())) {
                if (target instanceof LivingEntity livingEntity) {
                    livingEntity.damage(2, owner);
                    target.setFireTicks(40);
                }
                return true;
            }
            return false;
        }, 8, 2, false);
    }

    public void reflect(Entity reflector,Projectile projectile){
        if (!MetadataUtils.hasData(projectile, "reflected")) {
            if (projectile instanceof Arrow arrow) {
                if (arrow.isInBlock() || arrow.isOnGround()) {
                    return;
                }
            }
            if (projectile instanceof Fireball fireball) {
                if (fireball.getShooter() == null) {
                    fireball.setDirection(fireball.getDirection().multiply(-1));
                } else {
                    LivingEntity shooter = (LivingEntity) projectile.getShooter();
                    fireball.setDirection(MathUtils.direction(reflector.getLocation(), EntityUtils.getEntityDynamicLocation(shooter)));
                }
            } else if (projectile instanceof ShulkerBullet shulkerBullet) {
                if (shulkerBullet.getShooter() != null) {
                    shulkerBullet.setTarget((Entity) shulkerBullet.getShooter());
                }
            }
            if (projectile.getShooter() == null) {
                projectile.setVelocity(projectile.getVelocity().multiply(-1));
            } else {
                LivingEntity shooter = (LivingEntity) projectile.getShooter();
                projectile.setVelocity(MathUtils.direction(reflector.getLocation(), EntityUtils.getEntityDynamicLocation(shooter)));
            }
            MetadataUtils.setData(projectile, "reflected", true);
        }
    }
}
