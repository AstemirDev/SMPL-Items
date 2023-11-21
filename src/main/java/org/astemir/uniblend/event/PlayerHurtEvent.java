package org.astemir.uniblend.event;

import com.google.common.base.Function;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PlayerHurtEvent extends EntityDamageEvent {

    private static final HandlerList handlers = new HandlerList();
    private Player player;

    public PlayerHurtEvent(Player player, @NotNull DamageCause cause, double damage) {
        super(player, cause, damage);
        this.player = player;
    }

    public PlayerHurtEvent(Player player, @NotNull DamageCause cause, @NotNull Map<DamageModifier, Double> modifiers, @NotNull Map<DamageModifier, ? extends Function<? super Double, Double>> modifierFunctions) {
        super(player, cause, modifiers, modifierFunctions);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
