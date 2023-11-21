package org.astemir.uniblend.core.gui;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.astemir.uniblend.core.gui.slot.UGuiSlot;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.*;
import org.astemir.uniblend.core.Named;
import org.astemir.uniblend.core.gui.event.PlayerGuiSlotClickEvent;
import org.astemir.uniblend.core.gui.event.PlayerGuiSlotDragEvent;
import org.astemir.uniblend.core.gui.event.SlotClickInfo;
import org.astemir.uniblend.core.gui.event.SlotDragInfo;
import org.astemir.lib.jython.Scriptable;
import org.astemir.lib.jython.JythonScript;
import org.astemir.uniblend.misc.SoundInstance;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class UGui extends PropertyHolder implements Scriptable, Named {

    public static final UJsonDeserializer<UGui> DESERIALIZER = (json)->{
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("class")) {
                Class<? extends UGui> className = USerialization.getClass(UniblendGuis.INSTANCE, jsonObject, "class");
                return PropertyHolder.newInstance(className, jsonObject);
            }else{
                return PropertyHolder.newInstance(UGui.class, jsonObject);
            }
        }else{
            return UniblendGuis.INSTANCE.matchEntry(json.getAsString()).create();
        }
    };

    private static final CopyOnWriteArrayList<HumanEntity> NO_VIEWERS = new CopyOnWriteArrayList<>();
    @Property("dimension")
    private Size dimension = Size.SIZE_1x9;
    @Property("title")
    private Component title = Component.empty();
    @Property(value = "slots", type = UGuiSlot.class,load = LoadType.ARRAY)
    private UGuiSlot[] buttons = new UGuiSlot[]{};
    @Property("locked")
    private boolean locked = false;
    @Property(load = LoadType.CUSTOM)
    private JythonScript script;

    private boolean disposed = false;
    private String nameKey;
    private Inventory handle;

    @Override
    public void onCreate() {
        super.onCreate();
        script = loadPropertyFunc("script",String.class,(scriptStr)->{
            JythonScript script = new JythonScript(scriptStr,false);
            setScript(script);
            return script;
        },null);
    }

    public UGui slots(UGuiSlot... buttons) {
        if (buttons.length > 0) {
            this.buttons = buttons;
        }
        return this;
    }

    public UGuiSlot addSlot(UGuiSlot slot){
        if (buttons != null){
            List<UGuiSlot> slots = new ArrayList(Arrays.asList(buttons));
            slots.add(slot);
            this.buttons = slots.toArray(new UGuiSlot[slots.size()]);
        }else{
            this.buttons = new UGuiSlot[]{slot};
        }
        return slot;
    }

    public void clearSlots(){
        if (handle != null){
            for (UGuiSlot button : buttons) {
                removeItem(button.getIndex());
            }
        }
        this.buttons = new UGuiSlot[0];
    }

    public void update(long tick) {
        this.runFunc("on_update",null,this,tick);
    }

    public void playSound(SoundInstance soundInstance){
        for (HumanEntity viewer : getViewers()) {
            soundInstance.play(viewer.getLocation());
        }
    }

    public void playSound(Sound sound,float volume,float pitch){
        for (HumanEntity viewer : getViewers()) {
            viewer.getWorld().playSound(viewer.getLocation(),sound,volume,pitch);
        }
    }

    public void playSound(String soundName,float volume,float pitch){
        for (HumanEntity viewer : getViewers()) {
            viewer.getWorld().playSound(viewer.getLocation(),soundName,volume,pitch);
        }
    }

    public void show(Player player){
        if (handle == null) {
            disposed = false;
            handle = Bukkit.createInventory(null, dimension.size(), title);
            updateContent(false);
            UGuiHandler.INSTANCE.add(this);
        }
        player.openInventory(handle);
        this.runFunc("on_open",null,this);
    }
    public void setItem(int index, ItemStack itemStack){
        if (handle != null){
            handle.setItem(index,itemStack);
        }
    }

    public void removeItem(int index){
        if (handle != null){
            handle.setItem(index,null);
        }
    }

    public void consumeItem(int index,int count){
        if (handle != null){
            ItemStack itemStack = handle.getItem(index);
            if (itemStack != null){
                itemStack = itemStack.subtract(count);
            }
            handle.setItem(index,itemStack);
        }
    }

    public void updateContent(boolean clear){
        if (handle != null){
            if (clear) {
                handle.clear();
            }
            if (buttons.length > 0) {
                for (UGuiSlot button : buttons) {
                    if (button.getItemStack() != null) {
                        handle.setItem(button.getIndex(), button.getItemStack());
                    }
                }
            }
        }
    }


    public void dispose(){
        if (handle == null || getViewers().size() == 0){
            disposed = true;
            handle = null;
        }
    }

    public UGui lock() {
        this.locked = true;
        return this;
    }

    public boolean isViewing(HumanEntity entity){
        for (HumanEntity viewer : getViewers()) {
            if (viewer.getUniqueId().equals(entity.getUniqueId())){
                return true;
            }
        }
        return false;
    }

    public CopyOnWriteArrayList<HumanEntity> getViewers(){
        if (handle != null) {
            return new CopyOnWriteArrayList<>(handle.getViewers());
        }else{
            return NO_VIEWERS;
        }
    }

    public UGuiSlot getSlot(int slot){
        for (UGuiSlot button : buttons) {
            if (button.getIndex() == slot){
                return button;
            }
        }
        return null;
    }

    public UGuiSlot[] getButtons() {
        return buttons;
    }

    public Component getTitle() {
        return title;
    }

    public Inventory getHandle() {
        return handle;
    }

    public boolean isLocked() {
        return locked;
    }

    public Size getDimension() {
        return dimension;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    public void setLocked() {
        this.locked = true;
    }

    public void close(){
        for (HumanEntity viewer : getViewers()) {
            viewer.closeInventory();
        }
    }

    public void onClose(Player player) {
        this.runFunc("on_close",null,this);
    }

    public EventExecutionResult onDragSlot(UGuiSlot slot, Player player, SlotDragInfo dragInfo){
        slot.drag(this, player, dragInfo);
        EventExecutionResult defaultResult = runFunc("on_slot_drag",EventExecutionResult.from(!dragInfo.isCancelled()),new PlayerGuiSlotDragEvent(this,slot,player,dragInfo));
        if (defaultResult == EventExecutionResult.PROCEED){
            return EventExecutionResult.from(slot.isAccessible());
        }else{
            return defaultResult;
        }
    }

    public EventExecutionResult onClickSlot(UGuiSlot slot, Player player, SlotClickInfo clickInfo){
        slot.click(this,player, clickInfo);
        EventExecutionResult defaultResult = runFunc("on_slot_click",EventExecutionResult.from(!clickInfo.isCancelled()),new PlayerGuiSlotClickEvent(this,slot,player,clickInfo));
        if (defaultResult == EventExecutionResult.PROCEED){
            return EventExecutionResult.from(slot.isAccessible());
        }else{
            return defaultResult;
        }
    }

    public ItemStack getItemStack(int index){
        return handle.getItem(index);
    }

    @Override
    public JythonScript getScript() {
        return script;
    }

    @Override
    public void setScript(JythonScript script) {
        this.script = script;
        this.runFunc("init",null, this);
    }

    @Override
    public String getNameKey() {
        return nameKey;
    }

    @Override
    public void setNameKey(String key) {
        this.nameKey = key;
    }




    public static enum Size {
        SIZE_1x9(1,9),
        SIZE_2x9(2,9),
        SIZE_3x9(3,9),
        SIZE_4x9(4,9),
        SIZE_5x9(5,9),
        SIZE_6x9(6,9),
        SIZE_7x9(7,9);
        public static final UJsonDeserializer<Size> DESERIALIZER = (json)-> Size.fromString(json.getAsString());

        private int rows;
        private int cols;

        Size(int rows, int cols){
            this.rows = rows;
            this.cols = cols;
        }

        public int size(){
            return rows*cols;
        }

        public static Size fromString(String size) {
            switch (size) {
                case "1x9", "9":
                    return SIZE_1x9;
                case "2x9", "18":
                    return SIZE_2x9;
                case "3x9", "27":
                    return SIZE_3x9;
                case "4x9", "36":
                    return SIZE_4x9;
                case "5x9", "45":
                    return SIZE_5x9;
                case "6x9", "54":
                    return SIZE_6x9;
                case "7x9", "63":
                    return SIZE_7x9;
            }
            return SIZE_1x9;
        }
    }
}
