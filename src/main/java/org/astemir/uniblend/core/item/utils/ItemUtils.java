package org.astemir.uniblend.core.item.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.utils.NMSUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Map;

public class ItemUtils {

    public static ItemStack itemWithModel(Material material, int modelData){
        ItemStack itemStack = new ItemStack(material);
        itemStack.setCustomModelData(modelData);
        return itemStack;
    }


    public static ItemStack itemWithModelColor(Material material, int modelData, Color color){
        ItemStack itemStack = new ItemStack(material);
        itemStack.setCustomModelData(modelData);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof LeatherArmorMeta armorMeta){
            armorMeta.setColor(color);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setItemColor(ItemStack itemStack, Color color){
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof LeatherArmorMeta armorMeta){
            armorMeta.setColor(color);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static NamespacedKey getItemsAdderKey(ItemStack itemStack){
        if (itemStack != null){
            if (itemStack.getItemMeta() != null){
                CompoundTag tag = getTag(itemStack);
                if (tag.contains("itemsadder")){
                    CompoundTag itemsAdderTag = tag.getCompound("itemsadder");
                    return new NamespacedKey(itemsAdderTag.get("namespace").getAsString(),itemsAdderTag.get("id").getAsString());
                }
            }
        }
        return null;
    }

    public static boolean checkEquality(ItemStack a,ItemStack b,boolean vanilla){
        if (a.getType() == b.getType()){
            UItem uitemA = UniblendItems.getItem(a);
            UItem uitemB = UniblendItems.getItem(b);
            if (uitemA != null && uitemB != null){
                return uitemA.getNameKey().equals(uitemB.getNameKey());
            }
            NamespacedKey keyA = getItemsAdderKey(a);
            NamespacedKey keyB = getItemsAdderKey(b);
            if (keyA != null && keyB != null){
                return keyA.equals(keyB);
            }
            if (vanilla) {
                if (uitemA != null || uitemB != null || keyA != null || keyB != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static ItemStack setEnchantments(ItemStack itemStack,Map<Enchantment,Integer> enchantments){
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            itemStack.addUnsafeEnchantment(entry.getKey(), entry.getValue());
        }
        return itemStack;
    }

    public static ItemStack setAttributes(ItemStack itemStack,Map<Attribute, AttributeModifier> attributes){
        for (Map.Entry<Attribute, AttributeModifier> entry : attributes.entrySet()) {
            itemStack.addAttributeModifier(entry.getKey(), entry.getValue());
        }
        return itemStack;
    }

    public static CompoundTag nbtFromString(String nbt){
        try {
            return TagParser.parseTag(nbt);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack addNbt(ItemStack itemStack,String nbt){
        return addTagData(itemStack,nbtFromString(nbt));
    }

    public static ItemStack setNbt(ItemStack itemStack,String nbt){
        return setTagData(itemStack,nbtFromString(nbt));
    }

    public static ItemStack addTagData(ItemStack itemStack,CompoundTag additionalNbt){
        CompoundTag old = getTag(itemStack);
        for (String key : additionalNbt.getAllKeys()) {
            old.put(key,additionalNbt.get(key));
        }
        net.minecraft.world.item.ItemStack nmsItem = NMSUtils.convert(itemStack);
        nmsItem.setTag(old);
        return NMSUtils.convert(nmsItem);
    }

    public static ItemStack setTagData(ItemStack itemStack,CompoundTag nbt){
        net.minecraft.world.item.ItemStack nmsItem = NMSUtils.convert(itemStack);
        nmsItem.setTag(nbt);
        return NMSUtils.convert(nmsItem);
    }

    public static CompoundTag getTag(ItemStack stack){
        net.minecraft.world.item.ItemStack itemStack = NMSUtils.convert(stack);
        if (itemStack.getTag() == null){
            return new CompoundTag();
        }else{
            return itemStack.getTag();
        }
    }


    public static CompoundTag getTag(net.minecraft.world.item.ItemStack stack){
        if (stack.getTag() == null){
            return new CompoundTag();
        }else{
            return stack.getTag();
        }
    }


    public static void damageItem(Entity entity, ItemStack item, int damage){
        if (entity instanceof LivingEntity livingEntity){
            item.damage(damage,livingEntity);
        }
    }

    public static boolean isWoodLog(Material material){
        switch (material){
            case ACACIA_LOG:
            case BIRCH_LOG:
            case JUNGLE_LOG:
            case DARK_OAK_LOG:
            case SPRUCE_LOG:
            case OAK_LOG:
            case STRIPPED_ACACIA_LOG:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_SPRUCE_LOG:
            case STRIPPED_OAK_LOG:
                return true;
        }
        return false;
    }

}
