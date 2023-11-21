package org.astemir.uniblend.core.gui;

import org.astemir.uniblend.core.UniblendRegistry;
import org.bukkit.entity.Player;

public class UGuiHandler extends UniblendRegistry.Concurrent<UGui> {

    public static UGuiHandler INSTANCE;
    public UGuiHandler() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate(long tick) {
        for (UGui gui : getEntries()) {
            gui.dispose();
            if (gui.isDisposed()){
                remove(gui);
            }else{
                gui.update(tick);
            }
        }
    }


    public static UGui getGui(Player player){
        for (UGui entry : INSTANCE.getEntries()) {
            if (entry.isViewing(player)){
                return entry;
            }
        }
        return null;
    }
}
