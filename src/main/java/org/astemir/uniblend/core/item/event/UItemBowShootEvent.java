package org.astemir.uniblend.core.item.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class UItemBowShootEvent {

    private LivingEntity shooter;
    private ItemStack bow;
    private EquipmentSlot hand;

    private float force;

    public UItemBowShootEvent(LivingEntity shooter, ItemStack bow, EquipmentSlot hand, float force) {
        this.shooter = shooter;
        this.bow = bow;
        this.hand = hand;
        this.force = force;
    }

    public LivingEntity getShooter() {
        return shooter;
    }

    public ItemStack getBow() {
        return bow;
    }

    public EquipmentSlot getHand() {
        return hand;
    }

    public float getForce() {
        return force;
    }

    public void setForce(float force) {
        this.force = force;
    }
}
