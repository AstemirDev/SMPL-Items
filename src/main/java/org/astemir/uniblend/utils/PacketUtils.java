package org.astemir.uniblend.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PacketUtils {

    public static void sendPacket(Player player, Packet<?> packet){
        NMSUtils.convert(player).connection.send(packet);
    }

    public static void sendPacketSpawnEntity(net.minecraft.world.entity.Entity entity){
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketUtils.sendPacket(player,new ClientboundAddEntityPacket(entity));
        }
    }

    public static void sendPacketTeleportEntity(net.minecraft.world.entity.Entity entity){
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketUtils.sendPacket(player,new ClientboundTeleportEntityPacket(entity));
        }
    }

    public static void sendPacketTeleportEntity(Player player,net.minecraft.world.entity.Entity entity){
        PacketUtils.sendPacket(player,new ClientboundTeleportEntityPacket(entity));
    }

    public static void sendPacketUpdateMeta(net.minecraft.world.entity.Entity entity){
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacketUpdateMeta(player,entity);
        }
    }

    public static void sendPacketUpdateMeta(Player player,net.minecraft.world.entity.Entity entity){
        List<SynchedEntityData.DataValue<?>> oldData = entity.getEntityData().packDirty();
        if (oldData != null) {
            List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
            for (SynchedEntityData.DataValue<?> value : oldData) {
                if (value != null && value.value() != null) {
                    data.add(value);
                }
            }
            PacketUtils.sendPacket(player, new ClientboundSetEntityDataPacket(entity.getId(), data));
        }
    }

    public static void sendPacketHideEntity(Entity entity){
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketUtils.sendPacket(player,new ClientboundRemoveEntitiesPacket(entity.getEntityId()));
        }
    }

    public static void sendPacketHideEntity(net.minecraft.world.entity.Entity entity){
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketUtils.sendPacket(player,new ClientboundRemoveEntitiesPacket(entity.getId()));
        }
    }

    public static void sendPacketHideEntities(int... ids){
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketUtils.sendPacket(player,new ClientboundRemoveEntitiesPacket(ids));
        }
    }

    public static void sendPacketHideEntities(Entity... entity){
        int ids[] = new int[entity.length];
        for (int i = 0; i < entity.length; i++) {
            ids[i] = entity[i].getEntityId();
        }
        for (Player player : entity[0].getWorld().getPlayers()) {
            PacketUtils.sendPacket(player,new ClientboundRemoveEntitiesPacket(ids));
        }
    }

}
