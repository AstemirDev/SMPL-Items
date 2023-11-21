package org.astemir.uniblend.core.item.parent.builtin.tridents;

import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.particle.UParticleBeam;
import org.astemir.uniblend.core.entity.EntityTaskHandler;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.entity.utils.MetadataUtils;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class VoltaicTrident extends UItem {


    @Property("attack-damage")
    private int attackDamage = 5;
    @Property("explosion-power")
    private int power = 10;

    @Override
    public EventExecutionResult onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Trident){
            if (MetadataUtils.hasData(e.getEntity(),"voltaic")){
                strike(e);
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        strike(e);
                    }
                }.runTaskLater(UniblendCorePlugin.getPlugin(),4);
            }
        }
        return super.onProjectileHit(e);
    }

    private void strike(ProjectileHitEvent e){
        UParticleEffect color = new UParticleEffect(Particle.REDSTONE).size(2,2,2).color(Color.BLUE,2).renderTimes(50);
        UParticleEffect whiteExplosion = new UParticleEffect(Particle.FIREWORKS_SPARK).renderTimes(100).size(0.5f,0.5f,0.5f).speed(2f,2f,2f).randomSpeed();
        for (int i = 0;i<20;i++) {
            UParticleBeam.sendParticleLightning(new UParticleEffect(Particle.REDSTONE).color(Color.FUCHSIA,1),e.getEntity().getLocation(),new Vector(Math.cos(RandomUtils.randomInt(0,360)),Math.sin(RandomUtils.randomInt(0,360)),Math.sin(RandomUtils.randomInt(0,360))),2,(entity)->{
                LivingEntity shooter = (LivingEntity)e.getEntity().getShooter();
                if (!entity.getUniqueId().equals(shooter.getUniqueId())) {
                    entity.damage(attackDamage,shooter);
                    return true;
                }
                return false;
            },7,1f,false);
        }
        EntityUtils.explodeFirework(e.getEntity().getLocation(),power,FireworkEffect.builder().withColor(Color.BLUE).build());
        whiteExplosion.play(e.getEntity().getLocation());
        color.play(e.getEntity().getLocation());
    }

    @Override
    public EventExecutionResult onThrow(ProjectileLaunchEvent e) {
        if (e.getEntity() instanceof Trident){
            Trident trident = (Trident) e.getEntity();
            trident.setCustomName("Voltaic");
            MetadataUtils.setData(trident,"voltaic",true);
            EntityTaskHandler.INSTANCE.add(new EntityTaskHandler.EntityRunnable(trident) {
                @Override
                public void run(Entity entity, long ticks) {
                    entity.getLocation().getWorld().spawnParticle(Particle.REDSTONE,  entity.getLocation().clone().add(RandomUtils.randomFloat(-1f, 1f), 1.75f+RandomUtils.randomFloat(-1.5f,-0.25f), RandomUtils.randomFloat(-1f, 1f)), 0, 0, 0, 0,new Particle.DustOptions(Color.BLUE,1));
                }
            });
        }
        return super.onThrow(e);
    }

    @Override
    public EventExecutionResult onTick(Player player, ItemStack itemStack, long ticks) {
        if (ticks % 5 == 0){
            Location loc = player.getLocation();
            for (int i = 0;i<2;i++) {
                loc.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(RandomUtils.randomFloat(-1f, 1f), 2f+RandomUtils.randomFloat(-1.75f,-0.25f), RandomUtils.randomFloat(-1f, 1f)), 0, 0, 0, 0);
            }
        }
        return super.onTick(player,itemStack,ticks);
    }
}

