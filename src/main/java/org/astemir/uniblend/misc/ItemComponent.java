package org.astemir.uniblend.misc;

import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.item.UniblendItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class ItemComponent {

    public static final UJsonDeserializer<ItemComponent> DESERIALIZER = (json)-> ItemComponent.of(()-> USerialization.deserialize(json,ItemStack.class));
    private Supplier<ItemStack> itemStack;

    public ItemComponent(Supplier<ItemStack> itemStack) {
        this.itemStack = itemStack;
    }

    public boolean test(ItemStack otherStack,boolean vanilla){
        if (otherStack == null){
            return false;
        }
        ItemStack itemStack = getItemStack().get();
        return ItemUtils.checkEquality(itemStack,otherStack,vanilla);
    }


    public Supplier<ItemStack> getItemStack() {
        return itemStack;
    }

    public static ItemComponent of(ItemStack itemStack){
        return new ItemComponent(()->itemStack);
    }

    public static ItemComponent of(Material material){
        return new ItemComponent(()->new ItemStack(material));
    }

    public static ItemComponent of(Material material, int amount){
        return new ItemComponent(()->new ItemStack(material,amount));
    }

    public static ItemComponent of(String smplItemId){
        return new ItemComponent(()-> UniblendItems.INSTANCE.matchEntry(smplItemId).toItemStack());
    }

    public static ItemComponent of(Supplier<ItemStack> itemStack){
        return new ItemComponent(itemStack);
    }
}