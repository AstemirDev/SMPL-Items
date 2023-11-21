package org.astemir.uniblend.core.item.parent.builtin.armor;

import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.core.item.parent.UItemArmor;
import org.astemir.uniblend.event.EventExecutionResult;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

public class YurvelineArmor extends UItemArmor {
    @Override
    public EventExecutionResult onHurtByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity livingEntity && e.getDamager() instanceof EnderPearl) {
            if (UniblendItems.isFullSet(livingEntity, YurvelineArmor.class)) {
                return EventExecutionResult.CANCEL;
            }
        }
        return super.onEntityDamageByEntity(e);
    }

    @Override
    public EventExecutionResult onPotionAdded(EntityPotionEffectEvent e) {
        if (e.getNewEffect() != null) {
            if (e.getNewEffect().getType().equals(PotionEffectType.LEVITATION)){
                if (UniblendItems.isFullSet((LivingEntity) e.getEntity(), YurvelineArmor.class)) {
                    return EventExecutionResult.CANCEL;
                }
            }
        }
        return super.onPotionAdded(e);
    }
}
