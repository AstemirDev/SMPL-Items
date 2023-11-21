package org.astemir.uniblend.core.particle;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.format.TextColor;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Named;
import org.astemir.uniblend.utils.RandomUtils;
import org.astemir.uniblend.utils.ReflectionUtils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3d;

public class UParticleEffect<T> implements Named {

    public static final UJsonDeserializer<UParticleEffect> DESERIALIZER = (json)->{
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            Particle particle = USerialization.getEnum(jsonObject, "type",Particle.class);
            int renderTimes = USerialization.getOr(jsonObject, "times", Integer.class, 1);
            int count = USerialization.getOr(jsonObject, "count", Integer.class, 0);
            double delta = USerialization.getOr(jsonObject, "delta", Double.class, 0.0);
            Vector3d speed = USerialization.getOr(jsonObject, "speed", Vector3d.class, new Vector3d(0, 0, 0));
            Vector3d size = USerialization.getOr(jsonObject, "size", Vector3d.class, new Vector3d(0, 0, 0));
            boolean renderFar = USerialization.getBoolean(jsonObject, "render-far", true);
            boolean randomSpeed = USerialization.getBoolean(jsonObject, "random-speed", false);
            UParticleEffect particleEffect = new UParticleEffect(particle).
                    renderTimes(renderTimes).
                    delta(delta).
                    speed(speed.x, speed.y, speed.z).
                    count(count).
                    size(size.x, size.y, size.z);
            if (randomSpeed) {
                particleEffect.randomSpeed();
            }
            if (renderFar) {
                particleEffect.distant();
            }
            if (jsonObject.has("item")) {
                particleEffect.item(USerialization.get(jsonObject, "item", ItemStack.class));
            }
            if (jsonObject.has("block")) {
                particleEffect.block(ReflectionUtils.searchEnum(Material.class, USerialization.get(jsonObject, "block", String.class)));
            }
            if (jsonObject.has("color")) {
                float dustSize = USerialization.getOr(jsonObject, "dust-size", Float.class, 1f);
                TextColor color = USerialization.get(jsonObject, "color", TextColor.class);
                particleEffect.color(Color.fromRGB(color.red(), color.green(), color.blue()), dustSize);
            }
            return particleEffect;
        }else{
            return UniblendParticleEffects.INSTANCE.getEntry(json.getAsString());
        }
    };

    private Particle particle;
    private T particleData;
    private double speedX = 0;
    private double speedY = 0;
    private double speedZ = 0;
    private double sizeX = 0;
    private double sizeY = 0;
    private double sizeZ = 0;
    private double delta = 0;
    private int count = 0;
    private int renderTimes = 1;
    private boolean showFromDistance = false;
    private boolean randomSpeed = false;
    private String name;
    public UParticleEffect(Particle particle) {
        this.particle = particle;
    }

    public UParticleEffect speed(double x, double y, double z) {
        this.speedX = x;
        this.speedY = y;
        this.speedZ = z;
        return this;
    }

    public UParticleEffect size(double x, double y, double z) {
        this.sizeX = x;
        this.sizeY = y;
        this.sizeZ = z;
        return this;
    }

    public UParticleEffect count(int count) {
        this.count = count;
        return this;
    }

    public UParticleEffect renderTimes(int count) {
        this.renderTimes = count;
        return this;
    }

    public UParticleEffect delta(double delta) {
        this.delta = delta;
        return this;
    }

    public UParticleEffect item(ItemStack itemStack) {
        this.particleData = (T) itemStack;
        return this;
    }

    public UParticleEffect item(Material material, int count) {
        this.particleData = (T) new ItemStack(material, count);
        return this;
    }

    public UParticleEffect block(Material material) {
        this.particleData = (T) material.createBlockData();
        return this;
    }


    public UParticleEffect distant() {
        this.showFromDistance = true;
        return this;
    }

    public UParticleEffect randomSpeed() {
        this.randomSpeed = true;
        return this;
    }


    public UParticleEffect color(Color color, float size) {
        this.particleData = (T) new Particle.DustOptions(color, size);
        return this;
    }

    public void play(Location loc) {
        if (loc == null){
            return;
        }
        for (int i = 0; i < renderTimes; i++) {
            double sX = this.speedX;
            double sY = this.speedY;
            double sZ = this.speedZ;
            if (randomSpeed){
                sX*=RandomUtils.randomFloat(-1,1);
                sY*=RandomUtils.randomFloat(-1,1);
                sZ*=RandomUtils.randomFloat(-1,1);
            }
            if (count == 0) {
                if (delta != 0) {
                    loc.getWorld().spawnParticle(particle, loc.clone().add(RandomUtils.randomFloat(-(float) sizeX, (float) sizeX), RandomUtils.randomFloat(-(float) sizeY, (float) sizeY), RandomUtils.randomFloat(-(float) sizeZ, (float) sizeZ)), 0, sX, sY, sZ, delta, particleData, showFromDistance);
                }else{
                    if (particleData != null) {
                        loc.getWorld().spawnParticle(particle, loc.clone().add(RandomUtils.randomFloat(-(float) sizeX, (float) sizeX), RandomUtils.randomFloat(-(float) sizeY, (float) sizeY), RandomUtils.randomFloat(-(float) sizeZ, (float) sizeZ)), 0, sX, sY, sZ, particleData);
                    }else{
                        loc.getWorld().spawnParticle(particle, loc.clone().add(RandomUtils.randomFloat(-(float) sizeX, (float) sizeX), RandomUtils.randomFloat(-(float) sizeY, (float) sizeY), RandomUtils.randomFloat(-(float) sizeZ, (float) sizeZ)), 0, sX, sY, sZ);
                    }
                }
            } else {
                loc.getWorld().spawnParticle(particle, loc, count, sizeX, sizeY, sizeZ, delta, particleData, showFromDistance);
            }
        }
    }


    public static void play(Particle particle,Location loc, int count,float sizeX,float sizeY,float sizeZ,float speedX,float speedY,float speedZ,boolean randomSpeed,boolean showFromDistance) {
        play(particle,loc,count,sizeX,sizeY,sizeZ,speedX,speedY,speedZ,randomSpeed,0,showFromDistance,null);
    }


    public static void play(Particle particle,Location loc, int count,float sizeX,float sizeY,float sizeZ,float speedX,float speedY,float speedZ,boolean randomSpeed,float delta,boolean showFromDistance) {
        play(particle,loc,count,sizeX,sizeY,sizeZ,speedX,speedY,speedZ,randomSpeed,delta,showFromDistance,null);
    }

    public static <T> void play(Particle particle,Location loc, int count,float sizeX,float sizeY,float sizeZ,float speedX,float speedY,float speedZ,boolean randomSpeed,float delta,boolean showFromDistance,T data){
        for (int i = 0; i < count; i++) {
            double sX = speedX;
            double sY = speedY;
            double sZ = speedZ;
            if (randomSpeed){
                sX*=RandomUtils.randomFloat(-1,1);
                sY*=RandomUtils.randomFloat(-1,1);
                sZ*=RandomUtils.randomFloat(-1,1);
            }
            if (count == 0) {
                if (delta != 0) {
                    loc.getWorld().spawnParticle(particle, loc.clone().add(RandomUtils.randomFloat(-(float) sizeX, (float) sizeX), RandomUtils.randomFloat(-(float) sizeY, (float) sizeY), RandomUtils.randomFloat(-(float) sizeZ, (float) sizeZ)), 0, sX, sY, sZ, delta, data, showFromDistance);
                }else{
                    if (data != null) {
                        loc.getWorld().spawnParticle(particle, loc.clone().add(RandomUtils.randomFloat(-(float) sizeX, (float) sizeX), RandomUtils.randomFloat(-(float) sizeY, (float) sizeY), RandomUtils.randomFloat(-(float) sizeZ, (float) sizeZ)), 0, sX, sY, sZ, data);
                    }else{
                        loc.getWorld().spawnParticle(particle, loc.clone().add(RandomUtils.randomFloat(-(float) sizeX, (float) sizeX), RandomUtils.randomFloat(-(float) sizeY, (float) sizeY), RandomUtils.randomFloat(-(float) sizeZ, (float) sizeZ)), 0, sX, sY, sZ);
                    }
                }
            } else {
                loc.getWorld().spawnParticle(particle, loc, count, sizeX, sizeY, sizeZ, delta, data, showFromDistance);
            }
        }
    }

    @Override
    public String getNameKey() {
        return name;
    }

    @Override
    public void setNameKey(String key) {
        this.name = key;
    }
}