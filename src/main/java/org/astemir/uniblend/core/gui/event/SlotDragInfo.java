package org.astemir.uniblend.core.gui.event;

import org.astemir.uniblend.event.EventExecutionResult;
import org.bukkit.event.inventory.DragType;
import org.bukkit.inventory.ItemStack;

public class SlotDragInfo {

    private DragType dragType;
    private ItemStack cursorItem;
    private boolean changedCursor = false;
    private EventExecutionResult executionResult = EventExecutionResult.PROCEED;

    public SlotDragInfo(DragType dragType, ItemStack cursorItem) {
        this.dragType = dragType;
        this.cursorItem = cursorItem;
    }

    public void setCursorItem(ItemStack cursorItem) {
        this.cursorItem = cursorItem;
        this.changedCursor = true;
    }


    public boolean isDrag(DragType... types){
        for (DragType type : types) {
            if (dragType != type){
                return true;
            }
        }
        return false;
    }

    public ItemStack getCursorItem() {
        return cursorItem;
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

    public DragType getDragType() {
        return dragType;
    }
}
