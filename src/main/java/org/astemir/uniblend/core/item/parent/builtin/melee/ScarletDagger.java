package org.astemir.uniblend.core.item.parent.builtin.melee;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.item.parent.UItemSword;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ScarletDagger extends UItemSword {

    @Property("self-effect")
    private PotionEffect selfEffect = new PotionEffect(PotionEffectType.REGENERATION,100,1);
    @Property("effect-chance")
    private int effectChance = 50;
    @Override
    public EventExecutionResult onAttackEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity){
            if (RandomUtils.doWithChance(effectChance)){
                scarletEffect(e.getEntity().getLocation());
                ((LivingEntity)e.getDamager()).addPotionEffect(selfEffect);
            }
        }
        return super.onAttackEntity(e);
    }

    @Override
    public EventExecutionResult onTick(Player player, ItemStack itemStack, long ticks) {
        if (ticks % 10 == 0){
            Location loc = player.getLocation();
            for (int i = 0;i<3;i++) {
                loc.getWorld().spawnParticle(Particle.FALLING_DUST, loc.clone().add(RandomUtils.randomFloat(-0.5f, 0.5f), 1.75f, RandomUtils.randomFloat(-0.5f, 0.5f)), 0, 0, 0, 0, Material.REDSTONE_BLOCK.createBlockData());
            }
        }
        return super.onTick(player,itemStack,ticks);
    }

    private void scarletEffect(Location loc){
        loc.getWorld().playSound(loc,Sound.ENTITY_SQUID_DEATH,0.75f,0.5f);
        loc.getWorld().playSound(loc,Sound.ENTITY_SQUID_DEATH,1,1f);
        loc.getWorld().playSound(loc,Sound.ENTITY_BLAZE_SHOOT,1,0.75f);
        loc.getWorld().playSound(loc,Sound.ENTITY_BLAZE_SHOOT,1,2f);
        for (int i = 0;i<2;i++) {
            loc.getWorld().spawnParticle(Particle.ITEM_CRACK, loc.clone().add(0,0.5f,0), 20, 0.3f, 0.3f, 0.3f, 0.15f, new ItemStack(Material.REDSTONE_BLOCK));
            loc.getWorld().spawnParticle(Particle.ITEM_CRACK, loc.clone().add(0,0.5f,0), 40, 0.35f, 0.35f, 0.35f, 0.25f, new ItemStack(Material.ROTTEN_FLESH));
        }
    }
}
