package org.astemir.uniblend.core.projectile;

import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.PropertyHolder;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Named;
import org.astemir.uniblend.utils.MathUtils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.joml.Vector2d;

import java.util.function.Predicate;

public class UProjectile extends PropertyHolder implements Named {

    public static UJsonDeserializer<UProjectile> DESERIALIZER = (json)->{
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("class")) {
                Class<? extends UProjectile> className = USerialization.getClass(UniblendProjectiles.INSTANCE, jsonObject, "class");
                return PropertyHolder.newInstance(className, jsonObject);
            }else{
                return UProjectile.newInstance(UProjectile.class,jsonObject);
            }
        }else{
            return UniblendProjectiles.INSTANCE.matchEntry(json.getAsString()).create();
        }
    };

    @Property("size")
    private Vector2d size = new Vector2d(0.5f,0.5f);
    @Property("velocity-delta")
    private float velocityDelta = 0.25f;
    @Property("life")
    private int life = 40;
    @Property("ignore-liquids")
    private boolean ignoreLiquids = true;
    @Property("ignore-passable-blocks")
    private boolean ignorePassableBlocks = true;
    @Property("ignore-blocks")
    private boolean ignoreBlocks = true;

    private Entity shooter;
    private Location location;
    private Vector velocity = new Vector(0,0,0);
    private String nameKey;
    private boolean removed = false;
    private long ticks;
    private Predicate<Entity> filter = entity -> true;

    @Override
    public void onCreate() {
        UProjectileHandler.INSTANCE.add(this);
    }

    public void shoot(Entity shooter, Location location){
        this.shooter = shooter;
        this.location = location;
    }

    public void shoot(Location location){
        this.shoot(null,location);
    }

    public void update(){
        if (location == null){
            removed = true;
            return;
        }
        if (!velocity.isZero()) {
            RayTraceResult result = location.getWorld().rayTrace(location, velocity, size.y, ignoreLiquids ? FluidCollisionMode.NEVER : FluidCollisionMode.ALWAYS, ignorePassableBlocks, size.x, filter);
            if (result != null) {
                if (result.getHitBlock() != null) {
                    onHitBlock(result.getHitBlock(), result.getHitBlockFace());
                }
                if (result.getHitEntity() != null) {
                    onHitEntity(result.getHitEntity());
                }
            }
        }
        if (ticks >= life){
            kill();
        }
        Vector oldPos = getPosition();
        Vector newPos = MathUtils.lerp(oldPos,oldPos.clone().add(velocity),velocityDelta);
        setPosition((float) newPos.getX(), (float) newPos.getY(), (float) newPos.getZ());
        onUpdate(ticks);
        ticks++;
    }

    public void onUpdate(long ticks){}
    public void onDie(){}

    public void onHitBlock(Block block, BlockFace face){
        if (!ignoreBlocks){
            kill();
        }
    }
    public void onHitEntity(Entity entity){
        kill();
    }
    public void setPosition(float x,float y,float z){
        this.location = new Location(location.getWorld(),x,y,z);
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void kill(){
        removed = true;
        onDie();
    }

    public UProjectile filter(Predicate<Entity> filter) {
        this.filter = filter;
        return this;
    }

    public UProjectile ignorePassableBlocks(boolean ignorePassableBlocks) {
        this.ignorePassableBlocks = ignorePassableBlocks;
        return this;
    }

    public UProjectile ignoreBlocks(boolean ignoreBlocks) {
        this.ignoreBlocks = ignoreBlocks;
        return this;
    }

    public UProjectile ignoreLiquids(boolean ignoreLiquids) {
        this.ignoreLiquids = ignoreLiquids;
        return this;
    }

    @Override
    public void setNameKey(String key) {
        this.nameKey = key;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public Vector getPosition(){
        return new Vector(location.x(),location.y(),location.z());
    }

    public Location getLocation() {
        return location;
    }

    public long getTicks() {
        return ticks;
    }

    public Entity getShooter() {
        return shooter;
    }

    public boolean isRemoved() {
        return removed;
    }

    @Override
    public String getNameKey() {
        return nameKey;
    }

}

