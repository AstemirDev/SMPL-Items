package org.astemir.uniblend.core.item.parent.builtin.melee;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.item.parent.UItemSword;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.astemir.uniblend.utils.RandomUtils;

public class ChillingBlade extends UItemSword {

    @Property("attack-effect")
    private PotionEffect attackEffect = new PotionEffect(PotionEffectType.SLOW,160,0);

    @Override
    public EventExecutionResult onAttackEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity){
            World world = e.getEntity().getWorld();
            for (int i = 0;i<10;i++) {
                world.spawnParticle(Particle.SNOWBALL, e.getEntity().getLocation().getX()+ RandomUtils.randomFloat(-0.5f,0.5f), e.getEntity().getLocation().getY()+ 1f+RandomUtils.randomFloat(-0.5f,0.5f), e.getEntity().getLocation().getZ()+ RandomUtils.randomFloat(-0.5f,0.5f), 0, 0, 0, 0);
            }
            ((LivingEntity)e.getEntity()).addPotionEffect(attackEffect);
        }
        return super.onAttackEntity(e);
    }

}
