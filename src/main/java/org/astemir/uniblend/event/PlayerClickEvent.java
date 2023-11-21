package org.astemir.uniblend.event;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerClickEvent extends PlayerInteractEvent {

    private static final HandlerList handlers = new HandlerList();
    private Entity clickedEntity;
    private Vector clickedPosition;
    private boolean cancelled;

    public PlayerClickEvent(@NotNull Player who, @NotNull Action action, @Nullable ItemStack item, @Nullable Block clickedBlock, @NotNull BlockFace clickedFace, @Nullable Entity clickedEntity, @Nullable Vector clickedPosition, @Nullable EquipmentSlot hand) {
        super(who, action, item, clickedBlock, clickedFace,hand);
        this.clickedEntity = clickedEntity;
        this.clickedPosition = clickedPosition;
    }


    @Override
    public Vector getClickedPosition(){
        return clickedPosition;
    }

    public Entity getClickedEntity(){
        return clickedEntity;
    }

    public boolean isClickedOnEntity(){
        return clickedEntity != null;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
