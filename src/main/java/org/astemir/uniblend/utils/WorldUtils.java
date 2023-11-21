package org.astemir.uniblend.utils;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Collection;

public class WorldUtils {
    public static int getHours(World world){
        return (int) (world.getTime() / 1000L + 6L) % 24;
    }

    public static void tryBreak(Player player,Location loc, boolean dropItems){
        Collection<ItemStack> drops = loc.getBlock().getDrops(player.getInventory().getItemInMainHand());
        if (!drops.isEmpty()) {
            WorldUtils.spawnNaturalBlockParticle(loc, loc.getBlock().getType(), 10);
            loc.getBlock().setType(Material.AIR);
            if (dropItems) {
                for (ItemStack item : drops) {
                    loc.getWorld().dropItem(loc, item);
                }
            }
        }
    }


    public static void spawnNaturalBlockParticle(Location loc,Location blockLoc, int amount){
        loc.getWorld().playEffect(loc, Effect.STEP_SOUND,loc.getWorld().getBlockAt(blockLoc).getType(),amount);
    }

    public static void spawnNaturalBlockParticle(Location loc, Material material , int amount){
        loc.getWorld().playEffect(loc, Effect.STEP_SOUND,material,amount);
    }

    public static void breakBlockToEntity(World world,double x,double y,double z){
        Location loc = new Location(world,x,y,z);
        Block block = world.getBlockAt(loc);
        if (!block.isLiquid()) {
            FallingBlock fallingBlock = world.spawnFallingBlock(loc, block.getBlockData());
            fallingBlock.setFallDistance(1);
            fallingBlock.setTicksLived(1);
            fallingBlock.setDropItem(true);
            world.getBlockAt(loc).setType(Material.AIR);
        }
    }

    public static NamespacedKey getBiome(Location location) {
        World world = location.getWorld();
        ServerLevel nmsWorld = (ServerLevel) NMSUtils.convert(world);
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        Holder<Biome> holder = nmsWorld.getNoiseBiome(x >> 2, y >> 2, z >> 2);
        ResourceKey resourceKey= holder.unwrapKey().get();
        ResourceLocation resourceLocation = resourceKey.location();
        return new NamespacedKey(resourceLocation.getNamespace(),resourceLocation.getPath());
    }
}
