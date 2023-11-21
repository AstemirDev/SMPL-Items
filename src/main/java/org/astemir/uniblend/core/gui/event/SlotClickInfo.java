package org.astemir.uniblend.core.gui.event;

import org.astemir.uniblend.event.EventExecutionResult;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class SlotClickInfo {

    private ClickType clickType;
    private ItemStack cursorItem;
    private ItemStack currentItem;
    private InventoryAction inventoryAction;
    private EventExecutionResult executionResult = EventExecutionResult.PROCEED;
    private boolean changedCursor = false;
    private boolean changedCurrent = false;


    public SlotClickInfo(ClickType clickType, ItemStack cursorItem, ItemStack currentItem, InventoryAction inventoryAction) {
        this.clickType = clickType;
        this.cursorItem = cursorItem;
        this.currentItem = currentItem;
        this.inventoryAction = inventoryAction;
    }

    public void setCursorItem(ItemStack cursorItem) {
        this.cursorItem = cursorItem;
        this.changedCursor = true;
    }

    public void setCurrentItem(ItemStack currentItem) {
        this.currentItem = currentItem;
        this.changedCurrent = true;
    }

    public boolean isAction(InventoryAction... actions){
        for (InventoryAction action : actions) {
            if (inventoryAction == action){
                return true;
            }
        }
        return false;
    }

    public boolean isClick(ClickType... types){
        for (ClickType type : types) {
            if (clickType != type){
                return true;
            }
        }
        return false;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public ItemStack getCursorItem() {
        return cursorItem;
    }

    public ItemStack getCurrentItem() {
        return currentItem;
    }


    public InventoryAction getAction() {
        return inventoryAction;
    }

    public void cancel() {
        this.executionResult = EventExecutionResult.CANCEL;
    }

    public boolean isCancelled() {
        return executionResult.isCancelled();
    }

    public boolean isChangedCursor() {
        return changedCursor;
    }

    public boolean isChangedCurrent() {
        return changedCurrent;
    }
}
