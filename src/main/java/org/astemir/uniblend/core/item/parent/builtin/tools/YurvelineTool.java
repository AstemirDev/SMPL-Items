package org.astemir.uniblend.core.item.parent.builtin.tools;

import com.google.gson.JsonObject;
import net.kyori.adventure.util.TriState;
import org.astemir.uniblend.core.item.parent.UItemSword;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.MathUtils;
import org.astemir.uniblend.utils.PlayerUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Vector3d;

public class YurvelineTool extends UItemSword {

    @Property("magnet-area")
    private Vector3d magnetArea = new Vector3d(4,2,4);
    @Property("item-velocity")
    private double itemVelocity = 0.5;
    @Override
    public EventExecutionResult onTick(Player player, ItemStack itemStack, long tick) {
        for (Entity nearbyEntity : player.getLocation().getNearbyEntities(magnetArea.x, magnetArea.y, magnetArea.z)) {
            if (nearbyEntity instanceof Item item){
                float distance = (float) nearbyEntity.getLocation().distance(player.getLocation());
                if (distance > 1) {
                    Vector direction = MathUtils.direction(nearbyEntity.getLocation(), player.getLocation().clone().add(0,1,0)).multiply(itemVelocity);
                    item.setVelocity(direction);
                    item.setFrictionState(TriState.FALSE);
                }else{
                    PlayerUtils.itemGive(player,item.getItemStack());
                    item.remove();
                }
            }
        }
        return super.onTick(player,itemStack,tick);
    }
}
