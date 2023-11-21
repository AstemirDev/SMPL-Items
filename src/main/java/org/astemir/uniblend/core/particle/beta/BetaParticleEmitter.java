package org.astemir.uniblend.core.particle.beta;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.Named;
import org.astemir.uniblend.core.display.RenderFacing;
import org.astemir.uniblend.core.display.RenderProperty;
import org.astemir.uniblend.core.display.RenderSettings;
import org.astemir.uniblend.core.display.URenderer;
import org.astemir.uniblend.io.json.*;
import org.astemir.uniblend.misc.RandomizedColor;
import org.astemir.uniblend.misc.ValueRange;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class BetaParticleEmitter extends PropertyHolder implements Named {

    public static UJsonDeserializer<BetaParticleEmitter> DESERIALIZER = (json)->{
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            return PropertyHolder.newInstance(BetaParticleEmitter.class, jsonObject);
        }else{
            return UniblendBetaParticles.INSTANCE.matchEntry(json.getAsString()).create();
        }
    };

    @Property("speed")
    private ValueRange speed = new ValueRange(0.1f,0.1f);
    @Property("count")
    private ValueRange count = new ValueRange(1,1);
    @Property("life-time")
    private ValueRange lifeTime = new ValueRange(5,5);
    @Property("size")
    private ValueRange size = new ValueRange(0.5f,0.5f);
    @Property("squash")
    private ValueRange squash = new ValueRange(0,0);
    @Property("direction")
    private Vector3d direction = new Vector3d(0,1,0);
    @Property("volume")
    private Vector3d volume = new Vector3d(0.5f,0.5f,0.5f);
    @Property("color")
    private RandomizedColor color = null;
    @Property("rate")
    private int rate = 1;
    @Property("brightness")
    private int brightness = -1;
    @Property("rotation-speed")
    private int rotationSpeed = 0;
    @Property("rotation")
    private int rotation = 0;
    @Property("fixed-position")
    private boolean fixedPosition = false;
    @Property("constant-decreasing")
    private boolean constantDecreasing = true;
    @Property(value = "display")
    private RenderProperty displayProperties;
    private String name;
    private Location location;

    public void update(int ticks){
        if (location != null) {
            if (ticks % rate == 0) {
                for (int i = 0; i < count.get(); i++) {
                    createParticle();
                }
            }
        }
    }

    private void createParticle(){
        if (color != null) {
           displayProperties.randomizeColor(color);
        }
        Vector offset = new Vector(RandomUtils.randomFloat((float) -volume.x, (float) volume.x),RandomUtils.randomFloat((float) -volume.y, (float) volume.y),RandomUtils.randomFloat((float) -volume.z, (float) volume.z));
        new URenderer(location.clone().add(offset), new RenderSettings(displayProperties).rotation(new Quaternionf().rotationZ((float) Math.toRadians(rotation))).scale(getSize()).interpolationDelay(0).interpolationDuration(1).brightness(brightness).facing(RenderFacing.CENTERED)) {
            private final Vector direction = new Vector(BetaParticleEmitter.this.direction.x,BetaParticleEmitter.this.direction.y,BetaParticleEmitter.this.direction.z);
            private Vector velocity = new Vector(0,0,0);
            private final float speed = BetaParticleEmitter.this.speed.get();
            private final int lifeTime = (int) (BetaParticleEmitter.this.lifeTime.get());
            @Override
            public void update() {
                velocity = velocity.add(new Vector((float) direction.getX() * speed, (float) direction.getY() * speed, (float) direction.getZ() * speed));
                Vector nextPosition = getPosition();
                if (fixedPosition){
                    nextPosition = location.toVector().add(offset);
                }
                setPosition(nextPosition.add(velocity));
                settings().rotation(new Quaternionf(settings().getRotation()).rotateLocalZ((float) Math.toRadians(rotationSpeed)));
                if (constantDecreasing) {
                    settings().scale(settings().getScale().lerp(new Vector3f(0, 0, 0), 1 - getLerp(lifeTime, getTicks())));
                }
                if (getTicks() >= lifeTime) {
                    remove();
                }
                super.update();
            }
        };
    }

    public float getLerp(int lifeTime,int ticks){
        float remainingTime = lifeTime - ticks;
        float normalizedTime = remainingTime / lifeTime;
        float t = 1 - normalizedTime;
        float slowedTime = 1 - t * t;
        return slowedTime;
    }

    public Vector3f getSize(){
        float f1 = size.get();
        float f2 = squash.get();
        if (f2 != 0) {
            return new Vector3f(f1* (1f / f2), size.get()*f2, f1 * (1f / f2));
        }else{
            return new Vector3f(f1,f1,f1);
        }
    }
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
