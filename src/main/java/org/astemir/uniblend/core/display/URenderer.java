package org.astemir.uniblend.core.display;

import net.minecraft.world.entity.Display;
import net.minecraft.world.level.Level;
import org.astemir.uniblend.utils.NMSUtils;
import org.astemir.uniblend.utils.PacketUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public class URenderer {
    private Display display;
    private RenderSettings renderSettings;
    private int ticks = 0;
    private int id;

    public URenderer(Location location, RenderSettings renderSettings) {
        Level level = NMSUtils.convert(location.getWorld());
        this.renderSettings = renderSettings;
        this.display = (Display) renderSettings.getProperty().getEntityType().create(level);
        this.display.teleportTo(location.x(),location.y(),location.z());
        this.id = display.getId();
        renderSettings.apply(display);
        PacketUtils.sendPacketSpawnEntity(display);
        PacketUtils.sendPacketTeleportEntity(display);
        PacketUtils.sendPacketUpdateMeta(display);
        URenderHandler.INSTANCE.add(this);
    }

    public void update(){
        ticks++;
        if (display != null && !display.isRemoved()) {
            renderSettings.updateTransform(display);
        }
    }

    public void updateForPlayer(Player player) {
        PacketUtils.sendPacketUpdateMeta(player,display);
        PacketUtils.sendPacketTeleportEntity(player,display);
    }

    public void setInterpolationDelay(int delay){
        ((org.bukkit.entity.Display)display.getBukkitEntity()).setInterpolationDelay(delay);
        PacketUtils.sendPacketUpdateMeta(display);
    }

    public void setInterpolationDuration(int duration){
        ((org.bukkit.entity.Display)display.getBukkitEntity()).setInterpolationDuration(duration);
        PacketUtils.sendPacketUpdateMeta(display);
    }

    public void setPosition(float x, float y, float z){
        display.teleportTo(x,y,z);
    }

    public void setPosition(Location location){
        display.teleportTo(location.x(),location.y(), location.z());
    }

    public void setPosition(Vector position){
        display.teleportTo(position.getX(),position.getY(), position.getZ());
    }

    public Vector getPosition(){
        return new Vector(display.getX(),display.getY(),display.getZ());
    }

    public RenderSettings settings() {
        return renderSettings;
    }

    public void remove(){
        if (display != null){
            display.discard();
        }
        PacketUtils.sendPacketHideEntities(id);
        discard();
    }

    public void discard(){
        URenderHandler.INSTANCE.remove(this);
    }

    public int getId(){
        return display.getId();
    }

    public int getTicks() {
        return ticks;
    }
}
