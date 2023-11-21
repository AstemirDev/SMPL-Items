package org.astemir.uniblend.core.item.parent;


import org.astemir.uniblend.core.cooldown.UCooldownHandler;
import org.astemir.uniblend.core.entity.UniblendEntities;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.event.PlayerClickEvent;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.misc.SoundInstance;
import org.astemir.uniblend.utils.PlayerUtils;
import org.bukkit.Sound;

public class UItemBossSpawn extends UItem {
    @Property(value = "boss")
    private String bossId;
    @Property(value = "sound")
    private SoundInstance sound = new SoundInstance(Sound.ENTITY_WITHER_SPAWN,0.5f,1.5f);
    @Property("cooldown")
    private int cooldown = 40;

    @Override
    public EventExecutionResult onRightClick(PlayerClickEvent e) {
        if (UCooldownHandler.doWithCooldown(e.getPlayer(),getNameKey(),cooldown)){
            PlayerUtils.forceClick(e.getPlayer(),e.getHand());
            UniblendEntities.spawn(bossId,e.getPlayer().getLocation());
            sound.play(e.getPlayer().getLocation());
            e.getItem().subtract(1);
        }
        return super.onRightClick(e);
    }
}
