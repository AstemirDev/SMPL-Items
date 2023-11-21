package org.astemir.uniblend.core.display;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class RenderSettings {
    private Transformation transformation = new Transformation(new Vector3f(0,0,0),new Quaternionf(),new Vector3f(1,1,1),new Quaternionf());
    private RenderFacing facing = RenderFacing.FIXED;
    private int brightness = -1;
    private int interpolationDelay = -1;
    private int interpolationDuration = -1;
    private RenderProperty property;

    public RenderSettings(RenderProperty property) {
        this.property = property;
    }

    public void apply(Display display){
        display.setTransformation(transformation);
        display.setBillboardConstraints(facing.getConstraints());
        if (brightness != -1){
            display.setBrightnessOverride(new Brightness(brightness,brightness));
        }
        if (interpolationDelay != -1) {
            ((org.bukkit.entity.Display) display.getBukkitEntity()).setInterpolationDelay(interpolationDelay);
        }
        if (interpolationDuration != -1) {
            ((org.bukkit.entity.Display) display.getBukkitEntity()).setInterpolationDuration(interpolationDuration);
        }
        property.apply(display);
    }

    public void updateTransform(Display display){
        display.setTransformation(transformation);
    }

    public RenderSettings translation(Vector3f translation){
        this.transformation = new Transformation(translation,transformation.getLeftRotation(),transformation.getScale(),transformation.getRightRotation());
        return this;
    }

    public RenderSettings scale(Vector3f scale){
        this.transformation = new Transformation(transformation.getTranslation(),transformation.getLeftRotation(),scale,transformation.getRightRotation());
        return this;
    }

    public RenderSettings rotation(Quaternionf rotation){
        this.transformation = new Transformation(transformation.getTranslation(),transformation.getLeftRotation(),transformation.getScale(),rotation);
        return this;
    }

    public Vector3f getTranslation(){
        return transformation.getTranslation();
    }

    public Vector3f getScale(){
        return transformation.getScale();
    }

    public Quaternionf getRotation(){
        return transformation.getRightRotation();
    }

    public RenderSettings facing(RenderFacing facing) {
        this.facing = facing;
        return this;
    }

    public RenderSettings brightness(int brightness) {
        this.brightness = brightness;
        return this;
    }

    public RenderSettings interpolationDelay(int interpolationDelay) {
        this.interpolationDelay = interpolationDelay;
        return this;
    }

    public RenderSettings interpolationDuration(int interpolationDuration) {
        this.interpolationDuration = interpolationDuration;
        return this;
    }

    public RenderProperty getProperty() {
        return property;
    }

    public Transformation getTransformation() {
        return transformation;
    }
}
