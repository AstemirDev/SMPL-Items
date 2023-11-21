package org.astemir.uniblend.core.item.parent.builtin.tools;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.item.parent.UItemSword;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.RandomUtils;
import org.astemir.uniblend.utils.WorldUtils;
import org.bukkit.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.joml.Vector3d;

public class HeftyPickaxe extends UItemSword {

    @Property("area")
    private Vector3d area = new Vector3d(1,1,1);

    @Property("chance")
    private int chance = 25;

    @Override
    public EventExecutionResult onBreakBlock(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        for (int i = (int)-area.x;i<area.x;i++){
            for (int j = (int)-area.y;j<area.y;j++){
                for (int k = (int)-area.z;k<area.z;k++){
                    if (RandomUtils.doWithChance(chance)) {
                        Location newLoc = loc.clone().add(i, j, k);
                        if (!(newLoc.getBlockX() == loc.getBlockX() && newLoc.getBlockY() == loc.getBlockY() && newLoc.getBlockZ() == loc.getBlockZ())) {
                            WorldUtils.tryBreak(e.getPlayer(),newLoc,e.isDropItems());
                        }
                    }
                }
            }
        }
        return super.onBreakBlock(e);
    }
}

