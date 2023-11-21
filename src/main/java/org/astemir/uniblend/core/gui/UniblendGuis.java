package org.astemir.uniblend.core.gui;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.gui.command.CommandUGui;
import org.astemir.uniblend.core.gui.slot.UGuiSlot;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Registered;
import org.astemir.uniblend.core.UniblendRegistry;
import org.astemir.uniblend.core.gui.builtin.IncrustingTable;
import org.astemir.uniblend.core.gui.builtin.ItemMenu;
import org.astemir.uniblend.core.gui.builtin.TinkerTable;
import org.astemir.uniblend.core.gui.event.GuiEventListener;

import java.util.List;

@Registered("guis")
public class UniblendGuis extends UniblendRegistry.Default<UGui>{

    public static UniblendGuis INSTANCE;
    public UniblendGuis() {
        INSTANCE = this;
    }
    @Override
    public void onRegister() {
        registerEvent(new GuiEventListener());
        registerCommand(new CommandUGui());
    }

    @Override
    public void onSetupLookups() {
        setLookup("gui", UGui.class);
        setLookup("slot", UGuiSlot.class);
        setLookup("gui_crafting", GuiCrafting.class);
        setLookup("slot_result", GuiCrafting.ResultSlot.class);
        setLookup("item_menu", ItemMenu.class);
        setLookup("slot_next_page", ItemMenu.NextPage.class);
        setLookup("slot_prev_page", ItemMenu.PreviousPage.class);
        setLookup("tinker_table", TinkerTable.class);
        setLookup("incrusting_table", IncrustingTable.class);
        setLookup("incrust_button", IncrustingTable.IncrustButton.class);
        setLookup("incrust_result_slot", IncrustingTable.IncrustingResultSlot.class);
    }

    @Override
    public void onConfigLoad(List<PluginJsonConfig> configs) {
        clear();
        for (PluginJsonConfig config : configs) {
            JsonObject map = config.json();
            for (String key : map.keySet()) {
                INSTANCE.register(key, USerialization.deserialize(map.get(key), UGui.class));
            }
        }
    }
}
