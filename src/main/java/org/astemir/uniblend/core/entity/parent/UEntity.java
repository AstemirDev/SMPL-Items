package org.astemir.uniblend.core.entity.parent;

import com.google.gson.JsonObject;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import net.kyori.adventure.text.Component;
import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.core.entity.UEntityHandler;
import org.astemir.lib.meg.ModelBasedEntity;
import org.astemir.uniblend.core.entity.UniblendEntities;
import org.astemir.uniblend.core.entity.UniblendEntity;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.PropertyHolder;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Named;
import org.astemir.uniblend.utils.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class UEntity extends PropertyHolder implements UniblendEntity,Named, ModelBasedEntity {

    public static UJsonDeserializer<UEntity> DESERIALIZER = (json)->{
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            Class<? extends UEntity> className = USerialization.getClass(UEntityHandler.INSTANCE, jsonObject, "class");
            return PropertyHolder.newInstance(className, jsonObject);
        }else{
            return UniblendEntities.INSTANCE.matchEntry(json.getAsString()).create();
        }
    };

    @Property("type")
    private EntityType type = EntityType.PIG;
    @Property("natural-despawn")
    private boolean naturalDespawn = true;
    private String id;
    private long ticks;
    private Entity handle;
    private ActiveModel model;
    private ModeledEntity modeledEntity;

    public void save(Entity entity){}

    public void update(long globalTicks){
        ticks++;
        if (handle != null && !handle.isDead()) {
            onUpdateAlive(globalTicks);
        }
    }

    public void spawn(Location location){
        if (location.isChunkLoaded()) {
            handle = location.getWorld().spawnEntity(location, type);
            handle.customName(Component.translatable("entity.uniblend."+id));
            NBTUtils.set(handle,"id",id);
            NBTUtils.set(handle,"despawn",isNaturalDespawn());
            if (!isNaturalDespawn()) {
                handle.setPersistent(true);
                if (handle instanceof LivingEntity livingEntity) {
                    livingEntity.setRemoveWhenFarAway(false);
                }
            }
            trackForce();
            onSetup();
        }
    }

    public boolean isInGround(){
        if (getHandle() != null){
            return getHandle().getLocation().getBlock().isSolid();
        }
        return false;
    }
    public boolean isValid(){
        return getHandle() != null && getHandle().isValid();
    }

    public void remove(){
        if (handle != null){
            handle.remove();
        }
        removeModel();
        UEntityHandler.INSTANCE.remove(this);
    }

    public void setLocation(Location location) {
        if (handle != null) {
            handle.teleport(location);
        }
    }

    public float distanceTo(Location location){
        if (handle != null){
            return (float) getLocation().distanceSquared(location);
        }
        return 0;
    }

    public float distanceTo(Entity entity){
        return distanceTo(entity.getLocation());
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVelocity(Vector vector){
        if (getHandle() != null){
            getHandle().setVelocity(vector);
        }
    }

    public Vector getVelocity(){
        if (getHandle() != null){
            return getHandle().getVelocity();
        }
        return new Vector(0,0,0);
    }

    public void trackForce(){
        Bukkit.getScheduler().runTaskLater(UniblendCorePlugin.getPlugin(),()->{
            for (Entity nearbyEntity : getLocation().getNearbyEntities(20, 20, 20)) {
                if (nearbyEntity instanceof Player player){
                    onTracked(player);
                }
            }
        },2);
    }

    public void untrackForce(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            onUntracked(player);
        }
    }

    public Entity getHandle() {
        return handle;
    }

    public Location getLocation(){
        return handle.getLocation();
    }
    public long getTicks() {
        return ticks;
    }
    public boolean isNaturalDespawn() {
        return naturalDespawn;
    }
    @Override
    public String getNameKey() {
        return id;
    }
    @Override
    public void setNameKey(String key) {
        this.id = key;
    }

    @Override
    public void setModelEntity(ModeledEntity modelEntity) {
        this.modeledEntity = modelEntity;
    }

    @Override
    public void setModel(ActiveModel model) {
        this.model = model;
    }

    @Override
    public ActiveModel getModel() {
        return model;
    }

    @Override
    public ModeledEntity getModelEntity() {
        return modeledEntity;
    }
}
