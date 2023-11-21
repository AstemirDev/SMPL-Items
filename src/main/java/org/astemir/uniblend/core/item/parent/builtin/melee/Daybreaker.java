package org.astemir.uniblend.core.item.parent.builtin.melee;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.item.parent.UItemSword;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.astemir.uniblend.utils.RandomUtils;

public class Daybreaker extends UItemSword {

    @Property("gold-nugget-chance")
    private int goldNuggetChance = 50;
    @Property("gold-ingot-chance")
    private int goldIngotChance = 10;
    @Property("gold-nugget-max-count")
    private int goldNuggetMaxCount = 5;
    @Property("gold-ingot-max-count")
    private int goldIngotMaxCount = 5;
    @Override
    public EventExecutionResult onAttackEntity(EntityDamageByEntityEvent e) {
        World world = e.getEntity().getWorld();
        for (int i = 0;i<10;i++) {
            world.spawnParticle(Particle.ENCHANTMENT_TABLE, e.getEntity().getLocation().getX()+ RandomUtils.randomFloat(-0.5f,0.5f), e.getEntity().getLocation().getY()+ 1f+RandomUtils.randomFloat(-0.5f,0.75f), e.getEntity().getLocation().getZ()+ RandomUtils.randomFloat(-0.5f,0.5f), 0, 0, 0, 0);
        }
        for (int i = 0;i<2;i++) {
            world.playSound(e.getEntity().getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1, 0.5f);
            world.playSound(e.getEntity().getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1, 1.25f);
        }
        return super.onAttackEntity(e);
    }

    @Override
    public EventExecutionResult onEntityDeath(EntityDeathEvent e) {
        if (RandomUtils.doWithChance(goldNuggetChance)){
            e.getEntity().getWorld().dropItem(e.getEntity().getLocation(),new ItemStack(Material.GOLD_NUGGET,RandomUtils.randomInt(1,goldNuggetMaxCount)));
        }else
        if (RandomUtils.doWithChance(goldIngotChance)){
            e.getEntity().getWorld().dropItem(e.getEntity().getLocation(),new ItemStack(Material.GOLD_INGOT,RandomUtils.randomInt(1,goldIngotMaxCount)));
        }
        return super.onEntityDeath(e);
    }

}
