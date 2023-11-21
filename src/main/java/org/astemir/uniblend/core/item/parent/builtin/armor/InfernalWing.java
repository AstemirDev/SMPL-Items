package org.astemir.uniblend.core.item.parent.builtin.armor;


import com.google.gson.JsonObject;
import org.astemir.uniblend.core.item.parent.UItemArmor;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InfernalWing extends UItemArmor {

    @Property("velocity-multiplier")
    private double velocityMultiplier = 0.99;

    @Override
    public EventExecutionResult onTick(Player player, ItemStack itemStack, long ticks) {
        if (player.isGliding()) {
            player.setVelocity(player.getVelocity().multiply(velocityMultiplier));
        }
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
