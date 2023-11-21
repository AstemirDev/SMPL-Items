package org.astemir.uniblend.core.gui.event;

import org.astemir.uniblend.core.gui.UGui;
import org.astemir.uniblend.core.gui.slot.UGuiSlot;
import org.bukkit.entity.Player;

public class PlayerGuiSlotClickEvent {

    private UGui gui;
    private UGuiSlot guiSlot;
    private Player player;
    private SlotClickInfo clickInfo;

    public PlayerGuiSlotClickEvent(UGui gui, UGuiSlot guiSlot, Player player, SlotClickInfo clickInfo) {
        this.gui = gui;
        this.guiSlot = guiSlot;
        this.player = player;
        this.clickInfo = clickInfo;
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

    public SlotClickInfo getClickInfo() {
        return clickInfo;
    }
}
