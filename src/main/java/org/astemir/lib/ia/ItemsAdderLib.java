package org.astemir.lib.ia;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderLib {

    public static boolean isLoaded = false;

    public static NamespacedKey getKey(ItemStack itemStack){
        if (isLoaded) {
            CustomStack customStack = getCustomStack(itemStack);
            if (customStack != null) {
                return new NamespacedKey(customStack.getNamespace(), customStack.getId());
            }
        }
        return null;
    }

    public static void load(){
        isLoaded = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }

    public static boolean isCustom(ItemStack itemStack){
        if (isLoaded) {
            return getCustomStack(itemStack) != null;
        }
        return false;
    }

    public static boolean isCustom(String id){
        if (isLoaded) {
            return CustomStack.isInRegistry(id);
        }
        return false;
    }

    public static CustomStack getCustomStack(ItemStack itemStack){
        if (isLoaded) {
            return CustomStack.byItemStack(itemStack);
        }
        return null;
    }


    public static CustomStack getCustomStack(String id){
        if (isLoaded) {
            return CustomStack.getInstance(id);
        }
        return null;
    }


    public static ItemStack setDurability(ItemStack itemStack,int durability){
        if (isLoaded) {
            CustomStack customStack = CustomStack.byItemStack(itemStack);
            if (customStack != null) {
                customStack.setDurability(customStack.getMaxDurability() - durability);
                return customStack.getItemStack();
            }
        }
        return itemStack;
    }

    public static ItemStack copyDurability(ItemStack from, ItemStack to){
        if (isLoaded) {
            CustomStack customFrom = CustomStack.byItemStack(from);
            CustomStack customTo = CustomStack.byItemStack(to);
            if (customFrom != null && customTo != null) {
                customTo.setUsages(customFrom.getUsages());
                customTo.setDurability(customFrom.getDurability());
                return customTo.getItemStack();
            } else {
                return to;
            }
        }
        return to;
    }
}
