package org.astemir.uniblend.core.gui.event;

import org.astemir.uniblend.core.gui.UGuiHandler;
import org.astemir.uniblend.core.gui.UGui;
import org.astemir.uniblend.core.gui.slot.UGuiSlot;
import org.astemir.uniblend.event.EventExecutionResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class GuiEventListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        for (UGui gui : UGuiHandler.INSTANCE.getEntries()) {
            if (gui.isViewing(e.getPlayer())) {
                gui.onClose((Player)e.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onClick(InventoryClickEvent e){
        defaultClick(e);
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreativeClick(InventoryCreativeEvent e){
        defaultClick(e);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e){
        for (UGui gui : UGuiHandler.INSTANCE.getEntries()) {
            if (gui.isViewing(e.getWhoClicked())){
                boolean cancel = false;
                for (int slot : e.getRawSlots()) {
                    if (slot >= 0 && slot <= gui.getDimension().size()-1) {
                        UGuiSlot button = gui.getSlot(slot);
                        if (button != null) {
                            SlotDragInfo dragInfo = new SlotDragInfo(e.getType(),e.getCursor());
                            EventExecutionResult executionResult = gui.onDragSlot(button,(Player) e.getWhoClicked(),dragInfo);
                            if (dragInfo.isChangedCursor()) {
                                e.setCursor(dragInfo.getCursorItem());
                            }
                            if (!cancel) {
                                cancel = executionResult.isCancelled();
                            }
                        }else {
                            if (!cancel) {
                                cancel = gui.isLocked();
                            }
                        }
                    }
                }
                e.setCancelled(cancel);
            }
        }
    }

    public static void defaultClick(InventoryClickEvent e){
        for (UGui gui : UGuiHandler.INSTANCE.getEntries()) {
            if (gui.isViewing(e.getWhoClicked())){
                if (e.getRawSlot() >= 0 && e.getRawSlot() <= gui.getDimension().size()-1) {
                    UGuiSlot button = gui.getSlot(e.getRawSlot());
                    if (button != null) {
                        SlotClickInfo clickInfo = new SlotClickInfo(e.getClick(), e.getCursor(), e.getCurrentItem(), e.getAction());
                        EventExecutionResult executionResult = gui.onClickSlot(button,(Player) e.getWhoClicked(),clickInfo);
                        if (clickInfo.isChangedCursor()) {
                            e.setCursor(clickInfo.getCursorItem());
                        }
                        if (clickInfo.isChangedCurrent()) {
                            e.setCurrentItem(clickInfo.getCurrentItem());
                        }
                        e.setCancelled(executionResult.isCancelled());
                    } else {
                        e.setCancelled(gui.isLocked());
                    }
                }
            }
            if (e.isShiftClick()) {
                if (e.getClickedInventory() != gui.getHandle()) {
                    if (!e.isCancelled()) {
                        e.setCancelled(gui.isLocked());
                    }
                }
            }
        }
    }
}
