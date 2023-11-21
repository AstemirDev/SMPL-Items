package org.astemir.uniblend.core.item.parent.builtin.shields;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PrismaticShield extends UItem {

    @Property("damage-multiplier")
    private double damageMultiplier = 0.45;
    @Override
    public EventExecutionResult onHurtByEntity(EntityDamageByEntityEvent e) {
        Player player = (Player)e.getEntity();
        if (player.isBlocking()){
            if (e.getDamager() instanceof LivingEntity){
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_BREAK,1,0.5f);
                ((LivingEntity) e.getDamager()).damage(e.getDamage()*damageMultiplier,player);
            }
        }
        return super.onHurtByEntity(e);
    }

    @Override
    public EventExecutionResult onTick(Player player,ItemStack itemStack,long ticks) {
        if (ticks % 10 == 0){
            Location loc = player.getLocation();
            for (int i = 0;i<10;i++) {
                Color[] colors = new Color[]{Color.RED,Color.ORANGE,Color.YELLOW,Color.GREEN,Color.BLUE,Color.AQUA,Color.PURPLE};
                loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(RandomUtils.randomFloat(-1f, 1f), 1.75f+RandomUtils.randomFloat(-1.5f,-0.25f), RandomUtils.randomFloat(-1f, 1f)), 0, 0, 0, 0,new Particle.DustOptions(colors[RandomUtils.randomInt(0,colors.length)],RandomUtils.randomFloat(0.5f,2f)));
            }
            loc.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(RandomUtils.randomFloat(-1f, 1f), 1.75f+RandomUtils.randomFloat(-1.5f,-0.25f), RandomUtils.randomFloat(-1f, 1f)), 0, 0, 0, 0);
        }
        return super.onTick(player,itemStack,ticks);
    }
}
