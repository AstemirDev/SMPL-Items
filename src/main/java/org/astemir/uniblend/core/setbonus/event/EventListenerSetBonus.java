package org.astemir.uniblend.core.setbonus.event;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.setbonus.USetBonus;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.core.setbonus.UniblendSetBonuses;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class EventListenerSetBonus implements Listener {

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent e){
        disable(e.getPlayer(),e.getOffHandItem());
    }

    @EventHandler
    public void onArmorUnequipped(PlayerArmorChangeEvent e){
        disable(e.getPlayer(),e.getOldItem());
    }

    @EventHandler
    public void onItemChanged(PlayerItemHeldEvent e){
        disable(e.getPlayer(),e.getPlayer().getInventory().getItem(e.getPreviousSlot()));
    }

    @EventHandler
    public void onItemChanged(PlayerDropItemEvent e){
        disable(e.getPlayer(),e.getItemDrop().getItemStack());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player player = (Player)e.getWhoClicked();
        disable(player,player.getInventory().getItemInMainHand());
        disable(player,player.getInventory().getItemInOffHand());
        disable(player,e.getCurrentItem());
    }

    private void disable(Player player,ItemStack itemStack){
        UItem smplItem = UniblendItems.getItem(itemStack);
        if (smplItem != null) {
            for (USetBonus setBonus : UniblendSetBonuses.INSTANCE.getEntries()) {
                if (setBonus.canAffect(player,smplItem) && setBonus.isSetItem(smplItem)) {
                    setBonus.cure(player);
                }
            }
        }
    }
}
