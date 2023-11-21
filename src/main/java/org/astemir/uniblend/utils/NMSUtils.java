package org.astemir.uniblend.utils;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R2.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class NMSUtils {

    public static <T extends net.minecraft.world.item.Item> T convert(Item item){
        return (T) convert(item.getItemStack()).getItem();
    }

    public static <T extends net.minecraft.world.entity.Entity> T convert(Entity entity){
        return (T) ((CraftEntity)entity).getHandle();
    }

    public static ServerPlayer convert(HumanEntity entity){
        return ((CraftPlayer) entity).getHandle();
    }


    public static <T extends Entity> T convert(net.minecraft.world.entity.Entity entity){
        return (T)entity.getBukkitEntity();
    }

    public static World convert(net.minecraft.world.level.Level value){
        return value.getWorld();
    }

    public static net.minecraft.world.level.Level convert(World value){
        return ((CraftWorld)value).getHandle().getLevel();
    }

    public static net.minecraft.world.entity.EquipmentSlot convert(EquipmentSlot value){
        return CraftEquipmentSlot.getNMS(value);
    }

    public static net.minecraft.world.item.ItemStack convert(ItemStack value){
        return CraftItemStack.asNMSCopy(value);
    }
    public static ItemStack convert(net.minecraft.world.item.ItemStack value){
        return CraftItemStack.asBukkitCopy(value);
    }

    public static EquipmentSlot convert(net.minecraft.world.entity.EquipmentSlot value){
        return CraftEquipmentSlot.getSlot(value);
    }
}
