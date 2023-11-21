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

public class SacrificeSword extends UItemSword {

    @Property("attack-effect")
    private PotionEffect attackEffect = new PotionEffect(PotionEffectType.HARM,1,1,false,false);

    @Override
    public EventExecutionResult onAttackEntity(EntityDamageByEntityEvent e) {
        World world = e.getEntity().getWorld();
        if (e.getEntity() instanceof LivingEntity){
            for (int i = 0; i < 5; i++) {
                world.spawnParticle(Particle.SOUL, e.getEntity().getLocation().getX() + RandomUtils.randomFloat(-0.25f, 0.25f), e.getEntity().getLocation().getY() + 0.5f + RandomUtils.randomFloat(-0.25f, 0.25f), e.getEntity().getLocation().getZ() + RandomUtils.randomFloat(-0.25f, 0.25f), 10, 0,0,0);
            }
            for (int i = 0; i < 2; i++) {
                world.playSound(e.getEntity().getLocation(), Sound.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, 1, 1.5f);
                world.playSound(e.getEntity().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1, 2);
            }
            ((LivingEntity)e.getDamager()).addPotionEffect(attackEffect);
        }
        return super.onAttackEntity(e);
    }
}

