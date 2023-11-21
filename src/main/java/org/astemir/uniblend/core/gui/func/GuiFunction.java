package org.astemir.uniblend.core.gui.func;

import net.kyori.adventure.text.Component;
import org.astemir.uniblend.core.gui.UGui;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public abstract class GuiFunction {
    private static final LinkedList<GuiFunction> functions = new LinkedList<>();
    private static final GuiFunction MSG = new GuiFunction("msg") {
        private static void sendMessage(HumanEntity player,Object msgComponent){
            if (msgComponent instanceof String string){
                player.sendMessage(string);
            }
            if (msgComponent instanceof Component component){
                player.sendMessage(component);
            }
        }

        @Override
        void onExecute(UGui gui, Player player, Executor executor, Object[] arguments) {
            Object msgComponent = arguments[0];
            switch (executor){
                case PLAYER -> sendMessage(player,msgComponent);
                case ALL -> {
                    for (HumanEntity viewer : gui.getViewers()) {
                        sendMessage(viewer,msgComponent);
                    }
                }
            }
        }
    };

    private static final GuiFunction SOUND = new GuiFunction("sound") {

        private void playSound(HumanEntity player,String soundName,float volume,float pitch){
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            if (sound != null){
                player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
            }else {
                player.getWorld().playSound(player.getLocation(), soundName, volume, pitch);
            }
        }

        @Override
        void onExecute(UGui gui, Player player, Executor executor, Object[] arguments) {
            String soundName = arguments[0].toString();
            float volume = 1;
            float pitch = 1;
            if (arguments.length > 2){
                volume = ((Number)arguments[1]).floatValue();
            }
            if (arguments.length > 3){
                pitch = ((Number)arguments[2]).floatValue();
            }
            switch (executor){
                case PLAYER -> playSound(player,soundName,volume,pitch);
                case ALL -> {
                    for (HumanEntity viewer : gui.getViewers()) {
                        playSound(viewer,soundName,volume,pitch);
                    }
                }
            }
        }
    };

    private static final GuiFunction GIVE_ITEM = new GuiFunction("give") {

        private static void giveItem(HumanEntity entity, Object itemComponent){
            if (itemComponent instanceof ItemStack itemStack){
                PlayerUtils.itemGive(entity,itemStack);
            }
            if (itemComponent instanceof Material material){
                PlayerUtils.itemGive(entity,new ItemStack(material));
            }
            if (itemComponent instanceof String id){
                UItem itemSMPL = UniblendItems.INSTANCE.matchEntry(id);
                if (itemSMPL != null){
                    PlayerUtils.itemGive(entity,itemSMPL.toItemStack());
                }
            }
        }

        @Override
        void onExecute(UGui gui, Player player, Executor executor, Object[] arguments) {
            Object itemComponent = arguments[0];
            switch (executor){
                case PLAYER -> giveItem(player,itemComponent);
                case ALL -> {
                    for (HumanEntity viewer : gui.getViewers()) {
                        giveItem(viewer,itemComponent);
                    }
                }
            }
        }
    };


    private String functionName;
    public GuiFunction(String functionName) {
        this.functionName = functionName;
        functions.add(this);
    }
    abstract void onExecute(UGui gui, Player player, Executor executor, Object[] arguments);

    public static GuiFunction byName(String name){
        for (GuiFunction function : functions) {
            if (function.functionName.equals(name)){
                return function;
            }
        }
        return null;
    }


    public enum Executor{

        NONE("none"),
        PLAYER("@p"),
        ALL("@a");

        private String name;
        Executor(String name) {
            this.name = name;
        }

        public static Executor byName(String name){
            for (Executor value : values()) {
                if (value.name.equals(name)){
                    return value;
                }
            }
            return NONE;
        }
    }
}
