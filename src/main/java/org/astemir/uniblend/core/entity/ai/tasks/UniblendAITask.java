package org.astemir.uniblend.core.entity.ai.tasks;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public interface UniblendAITask {

    default boolean onInteract(Player player, EquipmentSlot hand, ItemStack itemStack){return true;}
    default boolean onHurt(EntityDamageEvent.DamageCause source, float damage){return true;}

    default void onStart(){}

    default void onUpdate(){}

    default void onStop(){}
}
