package org.astemir.uniblend.core.item;

import org.astemir.lib.jython.Scriptable;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.event.PlayerClickEvent;
import org.astemir.uniblend.event.PlayerHurtEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.python.antlr.op.Eq;

public interface UniblendItem extends Scriptable {

    default EventExecutionResult onRightClick(PlayerClickEvent e){
        return runFunc("on_right_click",EventExecutionResult.PROCEED,e);
    }

    default EventExecutionResult onLeftClick(PlayerClickEvent e){
        return runFunc("on_left_click",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onClickOther(PlayerClickEvent e){
        return runFunc("on_click_other",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onDrop(PlayerDropItemEvent e){
        return runFunc("on_drop",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onAttackEntity(EntityDamageByEntityEvent e){
        return runFunc("on_attack",EventExecutionResult.PROCEED,e);
    }

    default EventExecutionResult onConsume(PlayerItemConsumeEvent e){
        return runFunc("on_consume",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onConsumeOther(PlayerItemConsumeEvent e){
        return runFunc("on_consume_other",EventExecutionResult.PROCEED,e);
    }

    default EventExecutionResult onProjectileHit(ProjectileHitEvent e){
        return runFunc("on_projectile_hit",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onThrow(ProjectileLaunchEvent e){
        return runFunc("on_throw",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onInventoryClick(InventoryClickEvent e){
        return runFunc("on_inventory_click",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onSpawnInWorldAsEntity(EntitySpawnEvent e){
        return runFunc("on_spawn_as_entity",EventExecutionResult.PROCEED,e);
    }

    default EventExecutionResult onPotionAdded(EntityPotionEffectEvent e){
        return runFunc("on_potion_effect",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onJoin(PlayerJoinEvent e, EquipmentSlot slot) {
        return runFunc("on_join",EventExecutionResult.NO_RESULT,e,slot);
    }

    default EventExecutionResult onQuit(PlayerQuitEvent e, EquipmentSlot slot){
        return runFunc("on_quit",EventExecutionResult.NO_RESULT,e,slot);
    }

    default EventExecutionResult onBreakBlock(BlockBreakEvent e){
        return runFunc("on_block_break",EventExecutionResult.PROCEED,e);
    }

    default EventExecutionResult onHitByProjectile(ProjectileHitEvent e) {
        return runFunc("on_hit_by_projectile",EventExecutionResult.PROCEED,e);
    }

    default EventExecutionResult onEntityDamageByEntity(EntityDamageByEntityEvent e){
        return runFunc("on_entity_damaged_by_entity",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onEntityDeath(EntityDeathEvent e){
        return runFunc("on_kill_entity",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onSprint(PlayerToggleSprintEvent e){
        return runFunc("on_sprint",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onHurtByEntity(EntityDamageByEntityEvent e){
        return runFunc("on_hurt_by_entity",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onHurt(PlayerHurtEvent e){
        return runFunc("on_hurt",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onShoot(EntityShootBowEvent e){
        return runFunc("on_shoot",EventExecutionResult.PROCEED,e);
    }
    default EventExecutionResult onSneak(PlayerToggleSneakEvent e){
        return runFunc("on_sneak",EventExecutionResult.PROCEED,e);
    }

    default EventExecutionResult onItemSwap(PlayerSwapHandItemsEvent e,EquipmentSlot slot){
        return runFunc("on_swap",EventExecutionResult.PROCEED,e,slot);
    }

    default EventExecutionResult onItemChange(PlayerItemHeldEvent e,int slot){
        return runFunc("on_change",EventExecutionResult.PROCEED,e,slot);
    }

    default EventExecutionResult onTick(Player player, ItemStack itemStack, long tick){
        return runFunc("on_tick",EventExecutionResult.NO_RESULT,player,itemStack,tick);
    }
}
