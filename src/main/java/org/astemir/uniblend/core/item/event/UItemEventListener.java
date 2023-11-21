package org.astemir.uniblend.core.item.event;


import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.event.PlayerClickEvent;
import org.astemir.uniblend.event.PlayerHurtEvent;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.item.UniblendItems;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.python.antlr.op.Eq;

import java.util.Map;


public class UItemEventListener implements Listener {

    @EventHandler
    public void onInteract(PlayerClickEvent e){
        UItem[] items = UniblendItems.getFullItems(e.getPlayer());
        for (UItem item : items) {
            if (item != null){
                e.setCancelled(item.onClickOther(e).isCancelled());
            }
        }
        ItemStack itemStack = e.getItem();
        UItem item = UniblendItems.getItem(itemStack);
        if (item != null){
            boolean cancel = false;
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
                cancel = item.onRightClick(e).isCancelled();
            }else
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
                cancel = item.onLeftClick(e).isCancelled();
            }
            e.setCancelled(cancel);
        }
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent e){
        UItem itemA = UniblendItems.getItem(e.getMainHandItem());
        if (itemA != null){
            e.setCancelled(itemA.onItemSwap(e, EquipmentSlot.HAND).isCancelled());
        }
        UItem itemB = UniblendItems.getItem(e.getOffHandItem());
        if (itemB != null) {
            e.setCancelled(itemB.onItemSwap(e,EquipmentSlot.OFF_HAND).isCancelled());
        }
    }


    @EventHandler
    public void onItemChanged(PlayerItemHeldEvent e){
        ItemStack oldStack = e.getPlayer().getInventory().getItem(e.getPreviousSlot());
        ItemStack newStack = e.getPlayer().getInventory().getItem(e.getNewSlot());
        UItem itemA = UniblendItems.getItem(oldStack);
        if (itemA != null){
            e.setCancelled(itemA.onItemChange(e,e.getPreviousSlot()).isCancelled());
        }
        UItem itemB = UniblendItems.getItem(newStack);
        if (itemB != null){
            EventExecutionResult res = itemB.onItemChange(e,e.getNewSlot());
            if (!e.isCancelled()) {
                e.setCancelled(res.isCancelled());
            }
        }
    }


    @EventHandler
    public void onShoot(EntityShootBowEvent e){
        ItemStack itemStack = e.getBow();
        UItem item = UniblendItems.getItem(itemStack);
        if (item != null){
            e.setCancelled(item.onShoot(e).isCancelled());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        UItem[] items = UniblendItems.getFullItems(e.getPlayer());
        for (UItem item : items) {
            if (item != null){
                item.onBreakBlock(e);
            }
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent e){
        if (e.getEntity() instanceof LivingEntity livingEntity) {
            UItem[] items = UniblendItems.getFullItems(livingEntity);
            for (UItem item : items) {
                if (item != null) {
                    e.setCancelled(item.onPotionAdded(e).isCancelled());
                }
            }
        }
    }


    @EventHandler
    public void onSpawn(EntitySpawnEvent e){
        if (e.getEntity() instanceof org.bukkit.entity.Item) {
            ItemStack itemStack = ((org.bukkit.entity.Item) e.getEntity()).getItemStack();
            UItem item = UniblendItems.getItem(itemStack);
            if (item != null) {
                e.setCancelled(item.onSpawnInWorldAsEntity(e).isCancelled());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent e){
        ItemStack itemStack = e.getCursor();
        UItem item = UniblendItems.getItem(itemStack);
        if (item != null) {
            e.setCancelled(item.onInventoryClick(e).isCancelled());
        }else{
            ItemStack itemStackB = e.getCurrentItem();
            UItem itemB = UniblendItems.getItem(itemStackB);
            if (itemB != null){
                e.setCancelled(itemB.onInventoryClick(e).isCancelled());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onCreativeClick(InventoryCreativeEvent e){
        onInventoryClick(e);
    }

    @EventHandler
    public void onDispense(BlockDispenseArmorEvent e){
        ItemStack itemStack = e.getItem();
        UItem item = UniblendItems.getItem(itemStack);
        if (item != null){
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e){
        UItem[] items = UniblendItems.getFullItems(e.getPlayer());
        for (UItem item : items) {
            if (item != null){
                e.setCancelled(item.onConsumeOther(e).isCancelled());
            }
        }
        ItemStack itemStack = e.getItem();
        UItem item = UniblendItems.getItem(itemStack);
        if (item != null){
            e.setCancelled(item.onConsume(e).isCancelled());
        }
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent e){
        if (e.getEntity().getShooter() != null){
            if (e.getEntity().getShooter() instanceof LivingEntity livingEntity) {
                UItem[] items = UniblendItems.getFullItems(livingEntity);
                for (UItem item : items) {
                    if (item != null){
                        e.setCancelled(item.onThrow(e).isCancelled());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHitByProjectile(ProjectileHitEvent e){
        if (e.getHitEntity() instanceof Player player) {
            UItem[] items = UniblendItems.getFullItems(player);
            for (UItem item : items) {
                if (item != null){
                    e.setCancelled(item.onHitByProjectile(e).isCancelled());
                }
            }
        }
    }

    @EventHandler
    public void onThrownHit(ProjectileHitEvent e){
        if (e.getEntity().getShooter() != null){
            if (e.getEntity().getShooter() instanceof LivingEntity){
                ItemStack itemStack = null;
                if (e.getEntity() instanceof AbstractArrow){
                    itemStack = ((AbstractArrow)e.getEntity()).getItemStack();
                }else
                if (e.getEntity() instanceof ThrownPotion){
                    itemStack = ((ThrownPotion)e.getEntity()).getItem();
                }
                UItem item = UniblendItems.getItem(itemStack);
                if (item != null) {
                    e.setCancelled(item.onProjectileHit(e).isCancelled());
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        for (UItem item : UniblendItems.INSTANCE.getEntries()) {
            e.setCancelled(item.onEntityDamageByEntity(e).isCancelled());
        }
        if (e.getEntity() instanceof LivingEntity livingEntity) {
            UItem[] items = UniblendItems.getFullItems(livingEntity);
            for (UItem item : items) {
                if (item != null){
                    e.setCancelled(item.onHurtByEntity(e).isCancelled());
                }
            }
        }
        if (e.getDamager() instanceof LivingEntity livingEntity) {
            UItem[] items = UniblendItems.getFullItems(livingEntity);
            for (UItem item : items) {
                if (item != null) {
                    e.setCancelled(item.onAttackEntity(e).isCancelled());
                }
            }
        }
    }

    @EventHandler
    public void onHurt(PlayerHurtEvent e) {
        if (e.getCause() != null) {
            for (UItem item : UniblendItems.getFullItems(e.getPlayer())) {
                if (item != null) {
                    e.setCancelled(item.onHurt(e).isCancelled());
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player) {
            for (UItem item : UniblendItems.getFullItems( e.getEntity().getKiller().getPlayer())) {
                if (item != null) {
                    e.setCancelled(item.onEntityDeath(e).isCancelled());
                }
            }
        }
    }


    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        ItemStack itemStack = e.getItemDrop().getItemStack();
        UItem item = UniblendItems.getItem(itemStack);
        if (item != null){
            e.setCancelled(item.onDrop(e).isCancelled());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        Map<EquipmentSlot,UItem> equipment = UniblendItems.getCustomEquipment(player);
        for (EquipmentSlot equipmentSlot : equipment.keySet()) {
            UItem item = equipment.get(equipmentSlot);
            if (item != null) {
                item.onJoin(e,equipmentSlot);
            }
        }
    }

    @EventHandler
    public void onExit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        Map<EquipmentSlot,UItem> equipment = UniblendItems.getCustomEquipment(player);
        for (EquipmentSlot equipmentSlot : equipment.keySet()) {
            UItem item = equipment.get(equipmentSlot);
            if (item != null) {
                item.onQuit(e,equipmentSlot);
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e){
        Player player = e.getPlayer();
        for (UItem item : UniblendItems.getFullItems(player)) {
            if (item != null) {
                e.setCancelled(item.onSneak(e).isCancelled());
            }
        }
    }

    @EventHandler
    public void onSprint(PlayerToggleSprintEvent e){
        Player player = e.getPlayer();
        for (UItem item : UniblendItems.getFullItems(player)) {
            if (item != null) {
                e.setCancelled(item.onSprint(e).isCancelled());
            }
        }
    }
}
