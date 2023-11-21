package org.astemir.uniblend.core.item.parent.builtin.tools;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.event.PlayerClickEvent;
import org.astemir.uniblend.io.json.Property;
import org.joml.Vector3d;

public class YurvelineHoe extends YurvelineTool {
    @Property("durability-on-use")
    private int durabilityOnUse = 25;

    @Override
    public EventExecutionResult onRightClick(PlayerClickEvent e) {
        if (e.getClickedBlock() != null){
            if (!e.getClickedBlock().isSolid() && e.getClickedBlock().applyBoneMeal(e.getBlockFace())){
                e.getPlayer().swingHand(e.getHand());
                ItemUtils.damageItem(e.getPlayer(),e.getItem(),durabilityOnUse);
            }
        }
        return super.onRightClick(e);
    }
}
