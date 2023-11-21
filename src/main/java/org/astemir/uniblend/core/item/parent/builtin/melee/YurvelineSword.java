package org.astemir.uniblend.core.item.parent.builtin.melee;

import org.astemir.uniblend.core.cooldown.UCooldownHandler;
import org.astemir.uniblend.core.item.parent.UItemSword;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.event.PlayerClickEvent;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class YurvelineSword extends UItemSword {

    @Property("bonus-time")
    private int bonusTime = 80;

    @Property("bonus-damage")
    private double bonusDamage = 2;

    @Override
    public EventExecutionResult onClickOther(PlayerClickEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (!e.getPlayer().hasCooldown(Material.ENDER_PEARL) && e.getItem().getType() == Material.ENDER_PEARL){
                UCooldownHandler.INSTANCE.setCooldown(e.getPlayer(),"yurveline_sword",bonusTime);
            }
        }
        return super.onClickOther(e);
    }

    @Override
    public EventExecutionResult onAttackEntity(EntityDamageByEntityEvent e) {
        if (UCooldownHandler.INSTANCE.hasCooldown((LivingEntity) e.getDamager(),"yurveline_sword")){
            e.setDamage(e.getDamage()+bonusDamage);
        }
        return super.onAttackEntity(e);
    }
}
