package org.astemir.uniblend.core.entity.parent;

import com.destroystokyo.paper.entity.Pathfinder;
import com.google.gson.JsonObject;
import org.astemir.uniblend.core.entity.action.ActionController;
import org.astemir.uniblend.core.entity.action.IActionListener;
import org.astemir.uniblend.core.entity.ai.AITaskSystem;
import org.astemir.uniblend.event.EntitySubmitDeathDropEvent;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.misc.ItemDrops;
import org.astemir.uniblend.misc.SoundInstance;
import org.astemir.uniblend.misc.ValueRange;
import org.astemir.uniblend.utils.NMSUtils;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.checkerframework.checker.units.qual.K;

public class UMob extends UEntity implements IActionListener {
    @Property("ambient-sound")
    private SoundInstance ambientSound;
    @Property("hurt-sound")
    private SoundInstance hurtSound;
    @Property("death-sound")
    private SoundInstance deathSound;
    @Property("drops")
    private ItemDrops drops = new ItemDrops();
    @Property("health")
    private ValueRange health = new ValueRange(20);
    @Property("attack-damage")
    private ValueRange attackDamage = new ValueRange(1);
    @Property("attack-knockback")
    private ValueRange attackKnockback = new ValueRange(0.25f);
    @Property("armor")
    private ValueRange armor = new ValueRange(0);
    @Property("knockback-resistance")
    private ValueRange knockbackResistance = new ValueRange(0);
    private AITaskSystem taskSystem;
    private LivingEntity target;
    @Override
    public void onSetup() {
        resetGoals();
        taskSystem = new AITaskSystem(this);
        if (getHandle() instanceof Ageable ageable) {
            ageable.setAdult();
        }
        getHandle().getEquipment().clear();
        setSilent();
        setMaxHealth(health.get());
        setArmor(armor.get());
        setAttackDamage(attackDamage.get());
        setAttackKnockback(attackKnockback.get());
        setKnockbackResistance(knockbackResistance.get());
        onSetupAI(taskSystem);
    }

    @Override
    public void onUpdateAlive(long globalTicks) {
        taskSystem.update();
        for (ActionController controller : getControllers()) {
            controller.update();
        }
        if (globalTicks % 200 == 0 && RandomUtils.doWithChance(50)){
            if (ambientSound != null){
                ambientSound.play(getLocation());
            }
        }
    }

    @Override
    public void onHurt(EntityDamageEvent e) {
        if (!e.isCancelled()) {
            taskSystem.handleHurt(e.getCause(), (float) e.getDamage());
            if (hurtSound != null) {
                hurtSound.play(getLocation());
            }
        }
    }

    @Override
    public boolean onDeath(EntityDeathEvent e) {
        super.onDeath(e);
        if (deathSound != null) {
            deathSound.play(getLocation());
        }
        return false;
    }

    @Override
    public void onDrop(EntitySubmitDeathDropEvent e){
        drops.spawnDrops(getLocation());
    }

    @Override
    public void onClicked(PlayerInteractEntityEvent e) {}

    public void resetGoals(){
        net.minecraft.world.entity.Mob nmsMob = NMSUtils.convert(getHandle());
        nmsMob.targetSelector.removeAllGoals((g)->true);
        nmsMob.goalSelector.removeAllGoals((g)->true);
    }

    public void attack(LivingEntity livingEntity){
        if (getHandle() != null){
            getHandle().attack(livingEntity);
        }
    }

    public void addPotionEffect(PotionEffect effect){
        if (getHandle() != null){
            getHandle().addPotionEffect(effect);
        }
    }

    public void setRotation(float yaw,float pitch){
        if (getHandle() != null){
            getHandle().setRotation(yaw,pitch);
        }
    }

    public void lookAt(Location location){
        if (getHandle() != null){
            getHandle().lookAt(location);
        }
    }

    public void lookAt(Location location,float headRotationPitch,float maxHeadPitch){
        if (getHandle() != null){
            getHandle().lookAt(location,headRotationPitch,maxHeadPitch);
        }
    }

    public void lookAt(float x,float y,float z){
        if (getHandle() != null){
            getHandle().lookAt(x,y,z);
        }
    }

    public void lookAt(float x,float y,float z,float headRotationPitch,float maxHeadPitch){
        if (getHandle() != null){
            getHandle().lookAt(x,y,z,headRotationPitch,maxHeadPitch);
        }
    }

    public void lookAt(Entity entity){
        if (getHandle() != null){
            getHandle().lookAt(entity);
        }
    }

    public void lookAt(Entity entity,float headRotationPitch,float maxHeadPitch){
        if (getHandle() != null){
            getHandle().lookAt(entity,headRotationPitch,maxHeadPitch);
        }
    }

    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player player){
            return !(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR);
        }
        return true;
    }
    public boolean canTarget(Entity target) {
        if (target instanceof Player player){
            return !(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR);
        }
        return true;
    }

    public float distanceToTarget(){
        if (hasTarget()){
            return distanceTo(getTarget());
        }else{
            return 0;
        }
    }

    public float getHealth(){
        if (getHandle() != null){
            return (float)getHandle().getHealth();
        }
        return 0;
    }

    public float getMaxHealth(){
        if (getHandle() != null){
            return (float) getHandle().getMaxHealth();
        }
        return 0;
    }


    public void setAttackDamage(float value){
        if (getHandle() != null){
            if (getHandle().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) == null){
                getHandle().registerAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            }
            getHandle().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(value);
        }
    }


    public void setAttackKnockback(float value){
        if (getHandle() != null){
            if (getHandle().getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK) == null){
                getHandle().registerAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK);
            }
            getHandle().getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(value);
        }
    }

    public void setArmor(float value){
        if (getHandle() != null){
            if (getHandle().getAttribute(Attribute.GENERIC_ARMOR) == null){
                getHandle().registerAttribute(Attribute.GENERIC_ARMOR);
            }
            getHandle().getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(value);
        }
    }


    public void setKnockbackResistance(float value){
        if (getHandle() != null){
            if (getHandle().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE) == null){
                getHandle().registerAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
            }
            getHandle().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(value);
        }
    }

    public void setMaxHealth(float value){
        if (getHandle() != null){
            if (getHandle().getAttribute(Attribute.GENERIC_MAX_HEALTH) == null){
                getHandle().registerAttribute(Attribute.GENERIC_MAX_HEALTH);
            }
            getHandle().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(value);
            getHandle().setHealth(value);
        }
    }
    public void setSilent(){
        if (getHandle() != null) {
            getHandle().setSilent(true);
        }
    }

    public void setNoAI(){
        if (getHandle() != null) {
            getHandle().setAI(false);
        }
    }

    public void setNoGravity(){
        if (getHandle() != null){
            getHandle().setGravity(false);
        }
    }

    public void setNoPhysics(){
        if (getHandle() != null){
            NMSUtils.convert(getHandle()).noPhysics = true;
        }
    }
    public void setInvisible(){
        if (getHandle() != null) {
            getHandle().setInvisible(true);
        }
    }

    public Pathfinder getPathfinder(){
        if (getHandle() != null) {
            return getHandle().getPathfinder();
        }
        return null;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    public boolean hasTarget(){
        return target != null && target.isValid();
    }

    public AITaskSystem getTaskSystem() {
        return taskSystem;
    }

    public ItemDrops getDrops() {
        return drops;
    }

    @Override
    public Mob getHandle() {
        return (Mob) super.getHandle();
    }

    public float getViewDistance(){
        return 32;
    }

    public void onSetupAI(AITaskSystem ai){}
}
