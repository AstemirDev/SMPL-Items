package org.astemir.uniblend.core.gui.slot;


import com.google.gson.JsonObject;
import org.astemir.uniblend.core.gui.UniblendGuis;
import org.astemir.uniblend.core.gui.UGui;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.gui.event.SlotClickInfo;
import org.astemir.uniblend.core.gui.event.SlotDragInfo;
import org.astemir.uniblend.core.gui.func.GuiDemiScript;
import org.astemir.uniblend.io.json.PropertyHolder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class UGuiSlot extends PropertyHolder {

    public static final UJsonDeserializer<UGuiSlot> DESERIALIZER = (json)->{
        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.has("class")) {
            Class<? extends UGuiSlot> slotClass = USerialization.getClass(UniblendGuis.INSTANCE,jsonObject, "class");
            return PropertyHolder.newInstance(slotClass,jsonObject);
        }
        return PropertyHolder.newInstance(UGuiSlot.class,jsonObject);
    };

    @Property("index")
    private int index = 0;
    @Property("accessible")
    private boolean accessible = false;
    @Property("item")
    private ItemStack itemStack;
    private GuiClickHandler clickHandler = GuiClickHandler.EMPTY;
    private GuiDragHandler dragHandler = GuiDragHandler.EMPTY;
    private List<GuiDemiScript> commands = new ArrayList<>();

    public void click(UGui gui, Player player, SlotClickInfo clickInfo){
        for (GuiDemiScript command : commands) {
            command.run(gui,player);
        }
        clickHandler.onClick(gui,player,clickInfo);
    }

    public void drag(UGui gui, Player player, SlotDragInfo dragInfo){
        dragHandler.onDrag(gui,player,dragInfo);
    }

    public UGuiSlot setOnClick(GuiClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    public UGuiSlot setOnDrag(GuiDragHandler dragHandler) {
        this.dragHandler = dragHandler;
        return this;
    }

    public UGuiSlot accessible(){
        accessible = true;
        return this;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setCommands(List<GuiDemiScript> commands) {
        this.commands = commands;
    }

    public ItemStack getSlotItemContent(UGui gui){
        return gui.getItemStack(index);
    }
    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getIndex() {
        return index;
    }

    public interface GuiClickHandler {
        GuiClickHandler EMPTY = (gui,player,clickInfo) -> {};
        void onClick(UGui gui, Player player, SlotClickInfo clickInfo);
    }

    public interface GuiDragHandler {
        GuiDragHandler EMPTY = (gui,player,dragInfo) -> {};
        void onDrag(UGui gui, Player player, SlotDragInfo dragInfo);
    }
}
