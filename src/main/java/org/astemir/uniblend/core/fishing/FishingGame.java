package org.astemir.uniblend.core.fishing;

import net.kyori.adventure.text.Component;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Locale;

public class FishingGame {
    private UParticleEffect SPLASH = new UParticleEffect(Particle.WATER_SPLASH).size(0.1f,0.1f,0.1f).speed(2,0.25f,2).randomSpeed().renderTimes(20);
    private FishingBarElement[] elements;
    private FishDirection direction = FishDirection.LEFT;
    private ItemStack stack;
    private FishHook hook;
    private Location location;
    private double fishPower;
    private int exp;
    private int fishPosition = 0;
    private int greenPosition = 1;
    private boolean zAxis = false;


    public FishingGame(Location location,FishHook hook,ItemStack stack, int exp, int length,double power) {
        this.hook = hook;
        this.location = location;
        this.stack = stack;
        this.exp = exp;
        this.fishPower = Math.min(power,1);
        this.elements = new FishingBarElement[length];
        for (int i = 0; i < elements.length; i++) {
            FishingBarElement element = new FishingBarElement();
            if (i == 0){
                element = element.type(FishingBarElementType.LEFT);
            }else
            if (i == elements.length-1){
                element = element.type(FishingBarElementType.RIGHT);
            }else{
                element = element.type(FishingBarElementType.CENTER);
            }
            elements[i] = element;
        }
        randomizeGreen();
    }

    public void pool(Player player){
        player.playSound(player,Sound.ENTITY_FISHING_BOBBER_SPLASH,1,1);
        if (fishPosition == greenPosition){
            player.sendMessage(FishingHandler.INSTANCE.getMessages().get("fishing-success"));
            player.getInventory().addItem(stack);
            player.giveExp(exp);
            FishingHandler.INSTANCE.getSuccessSound().play(player.getLocation());
        }else{
            player.sendMessage(FishingHandler.INSTANCE.getMessages().get("fishing-failure"));
            FishingHandler.INSTANCE.getFailedSound().play(player.getLocation());
        }
        FishingHandler.INSTANCE.stopPlayingFishGame(player);
    }

    private Vector rotateVector(Vector v,float rotation){
        float angle = (float) Math.toRadians(rotation);
        double rotatedX = (v.getX()*Math.cos(angle)-v.getZ()*Math.sin(angle));
        double rotatedZ = (v.getZ()*Math.cos(angle)+v.getX()*Math.sin(angle));
        return new Vector(rotatedX,v.getY(),rotatedZ);
    }


    public void render(Player player, long ticks){
        if (hook != null){
            float rotation = player.getLocation().getYaw();
            int dir = direction == FishDirection.LEFT ? -1 : 1;
            if (RandomUtils.randomInt(300) == 0){
                zAxis = !zAxis;
            }
            Vector velocity;
            if (!zAxis) {
                velocity = rotateVector(new Vector(((float) dir) / 15f, hook.getVelocity().getY(), 0), rotation);
            }else{
                velocity = rotateVector(new Vector(0, hook.getVelocity().getY(),( (float) dir) / 15f), rotation);
            }
            if (hook.getLocation().clone().add(velocity).getBlock().isLiquid()){
                hook.setVelocity(velocity);
            }
            SPLASH.play(hook.getLocation());
        }
        if (((float)ticks) % (1f/fishPower) == 0) {
            int nextPos = fishPosition;
            if (direction == FishDirection.LEFT){
                nextPos -= 1;
            }else
            if (direction == FishDirection.RIGHT){
                nextPos +=1;
            }
            if (nextPos < 0){
                nextPos = 0;
                direction = FishDirection.RIGHT;
            }else
            if (nextPos > elements.length-1){
                nextPos = elements.length-1;
                direction = FishDirection.LEFT;
            }
            fishPosition = nextPos;
        }
        Component renderData = Component.text("");
        for (int i = 0; i < elements.length; i++) {
            FishingBarElement element = elements[i];
            String spriteStr = element.getSprite(direction,i == fishPosition,i == greenPosition);
            Component sprite = Component.text(spriteStr);
            Component empty = Component.text(FishingHandler.INSTANCE.getUnicode().get("empty"));
            renderData = renderData.append(sprite).append(empty);
        }
        player.sendActionBar(renderData);
    }

    public Location getLocation() {
        return location;
    }

    public FishHook getHook() {
        return hook;
    }

    public void randomizeGreen(){
        greenPosition = RandomUtils.randomInt(1,elements.length-2);
    }

    private class FishingBarElement{

        private FishingBarElementType type = FishingBarElementType.CENTER;

        public FishingBarElement type(FishingBarElementType type){
            this.type = type;
            return this;
        }
        public String getSprite(FishDirection fishDirection, boolean hasFish, boolean isGreen) {
            StringBuilder spriteKey = new StringBuilder();
            spriteKey.append(isGreen ? "bar-green" : "bar");
            spriteKey.append("-");
            spriteKey.append(type.name().toLowerCase());
            spriteKey.append("-");
            spriteKey.append(hasFish ? "fish" : "empty");
            if (hasFish) {
                spriteKey.append("-");
                spriteKey.append(fishDirection.name().toLowerCase());
            }
            return FishingHandler.INSTANCE.getUnicode().get(spriteKey.toString());
        }
    }

    private enum FishingBarElementType{
        LEFT,RIGHT,CENTER
    }

    private enum FishDirection{
        LEFT,RIGHT
    }
}
