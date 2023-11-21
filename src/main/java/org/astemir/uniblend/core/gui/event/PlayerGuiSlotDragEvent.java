package org.astemir.uniblend.core.gui.event;

import org.astemir.uniblend.core.gui.UGui;
import org.astemir.uniblend.core.gui.slot.UGuiSlot;
import org.bukkit.entity.Player;

public class PlayerGuiSlotDragEvent {

    private UGui gui;
    private UGuiSlot guiSlot;
    private Player player;
    private SlotDragInfo dragInfo;

    public PlayerGuiSlotDragEvent(UGui gui, UGuiSlot guiSlot, Player player, SlotDragInfo dragInfo) {
        this.gui = gui;
        this.guiSlot = guiSlot;
        this.player = player;
        this.dragInfo = dragInfo;
    }

    public UGui getGui() {
        return gui;
    }

    public UGuiSlot getSlot() {
        return guiSlot;
    }

    public Player getPlayer() {
        return player;
    }

    public SlotDragInfo getDragInfo() {
        return dragInfo;
    }
}
