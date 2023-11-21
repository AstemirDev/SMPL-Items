package org.astemir.uniblend.core.item.parent.builtin.melee;


import com.google.gson.JsonObject;
import org.astemir.uniblend.core.item.parent.UItemSword;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WitherBlade extends UItemSword {

    @Property("attack-effect")
    private PotionEffect attackEffect = new PotionEffect(PotionEffectType.WITHER,300,0);
    @Property("effect-chance")
    private int effectChance = 50;

    @Override
    public EventExecutionResult onAttackEntity(EntityDamageByEntityEvent e) {
        World world = e.getEntity().getWorld();
        if (e.getEntity() instanceof LivingEntity){
            if (RandomUtils.doWithChance(effectChance)) {
                for (int i = 0; i < 10; i++) {
                    world.spawnParticle(Particle.SMOKE_LARGE, e.getEntity().getLocation().getX() + RandomUtils.randomFloat(-0.25f, 0.25f), e.getEntity().getLocation().getY() + 0.5f + RandomUtils.randomFloat(-0.25f, 0.25f), e.getEntity().getLocation().getZ() + RandomUtils.randomFloat(-0.25f, 0.25f), 20, 0.05f, 0.05f, 0.05f);
                    world.spawnParticle(Particle.SOUL_FIRE_FLAME, e.getEntity().getLocation().getX() + RandomUtils.randomFloat(-0.25f, 0.25f), e.getEntity().getLocation().getY() + 0.5f + RandomUtils.randomFloat(-0.25f, 0.25f), e.getEntity().getLocation().getZ() + RandomUtils.randomFloat(-0.25f, 0.25f), 10, 0.05f, 0.05f, 0.05f);
                }
                for (int i = 0; i < 2; i++) {
                    world.playSound(e.getEntity().getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1.15f);
                    world.playSound(e.getEntity().getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 2);
                }
                ((LivingEntity)e.getEntity()).addPotionEffect(attackEffect);
            }
        }
        return super.onAttackEntity(e);
    }
}

