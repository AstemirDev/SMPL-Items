package org.astemir.uniblend.core.display;



import org.astemir.uniblend.core.UniblendRegistry;
import org.astemir.uniblend.utils.ArrayUtils;
import org.astemir.uniblend.utils.PacketUtils;
import org.bukkit.entity.Player;


public class URenderHandler extends UniblendRegistry.Concurrent<URenderer>{

    public static URenderHandler INSTANCE;
    public URenderHandler() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        int ids[] = new int[0];
        for (URenderer entry : INSTANCE.getEntries()) {
            ids = ArrayUtils.add(ids,entry.getId());
        }
        PacketUtils.sendPacketHideEntities(ids);
    }

    @Override
    public void onUpdate(long tick) {
        for (URenderer entry : INSTANCE.getEntries()) {
            entry.update();
        }
    }

    @Override
    public void onUpdatePerPlayer(Player player, long tick) {
        for (URenderer entry : INSTANCE.getEntries()) {
            if (entry.getPosition().distance(player.getLocation().toVector()) < 100) {
                entry.updateForPlayer(player);
            }
        }
    }
}
