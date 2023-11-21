package org.astemir.uniblend.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntitySubmitDeathDropEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private List<ItemStack> drops;
    private int droppedExp;
    private boolean cancelled = false;

    public EntitySubmitDeathDropEvent(@NotNull LivingEntity entity, @NotNull List<ItemStack> drops, int droppedExp) {
        super(entity);
        this.drops = drops;
        this.droppedExp = droppedExp;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public int getDroppedExp() {
        return droppedExp;
    }

    @Override
    public @NotNull LivingEntity getEntity() {
        return (LivingEntity) super.getEntity();
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
