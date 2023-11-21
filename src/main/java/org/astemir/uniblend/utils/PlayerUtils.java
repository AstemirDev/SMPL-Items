package org.astemir.uniblend.utils;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import org.astemir.uniblend.event.UniblendEventListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerUtils {

    public static Location getTrueLocation(Player player){
        Vector input = UniblendEventListener.getMotion(player);
        return player.getLocation().clone().add(input).add(player.getVelocity());
    }
    public static Vector mountInput(Player player){
        net.minecraft.world.entity.player.Player player1 = NMSUtils.convert(player);
        return new Vector(player1.xxa,player1.yya,player1.zza);
    }

    public static void forceClick(Player player, EquipmentSlot hand){
        player.swingHand(hand);
    }
    public static void itemGive(HumanEntity entity,ItemStack itemStack){
        net.minecraft.world.entity.player.Player nmsPlayer = NMSUtils.convert(entity);
        net.minecraft.world.item.ItemStack nmsItemStack = NMSUtils.convert(itemStack);
        boolean flag = nmsPlayer.getInventory().add(nmsItemStack);
        ItemEntity entityItem;
        if (flag && nmsItemStack.isEmpty()) {
            nmsItemStack.setCount(1);
            entityItem = nmsPlayer.drop(nmsItemStack, false, false, false);
            if (entityItem != null) {
                entityItem.makeFakeItem();
            }
            nmsPlayer.level().playSound(null, nmsPlayer.getX(), nmsPlayer.getY(), nmsPlayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((nmsPlayer.getRandom().nextFloat() - nmsPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            nmsPlayer.containerMenu.broadcastChanges();
        } else {
            entityItem = nmsPlayer.drop(nmsItemStack, false);
            if (entityItem != null) {
                entityItem.setNoPickUpDelay();
                entityItem.setTarget(nmsPlayer.getUUID());
            }
        }
    }

    public static void setPlayerTabName(String name, TextComponent tabName){
        Player player = Bukkit.getPlayer(name);
        if (player != null){
            player.playerListName(tabName);
        }
    }


    public static void sendMessage(String playerName,String message){
        Bukkit.getPlayer(playerName).sendMessage(message);
    }

    public static boolean isBossbarShown(Player player,BossBar bossBar){
        for (BossBar activeBossBar : player.activeBossBars()) {
            if (activeBossBar == bossBar){
                return true;
            }
        }
        return false;
    }

    public static ItemStack[] getHotbarItems(Player player){
        ItemStack[] res = new ItemStack[9];
        for (int i = 0;i<res.length;i++){
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack == null){
                res[i] = null;
            }else
            if (itemStack.getType().isAir()){
                res[i] = null;
            }else {
                res[i] = itemStack;
            }
        }
        return res;
    }

}
