package org.astemir.uniblend.core.entity.parent.builtin;

import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import org.astemir.uniblend.core.entity.parent.UMob;
import org.astemir.uniblend.core.entity.action.Action;
import org.astemir.uniblend.core.entity.action.ActionController;
import org.astemir.uniblend.core.entity.ai.AITaskSystem;
import org.astemir.uniblend.core.entity.ai.tasks.AITask;
import org.astemir.uniblend.core.entity.ai.tasks.AITaskAttack;
import org.astemir.uniblend.core.entity.ai.tasks.AITaskFindTarget;
import org.astemir.uniblend.core.entity.ai.tasks.AITaskTimer;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.misc.SoundInstance;
import org.astemir.uniblend.misc.ValueRange;
import org.astemir.uniblend.utils.MathUtils;
import org.astemir.uniblend.utils.NMSUtils;
import org.astemir.uniblend.utils.RandomUtils;
import org.astemir.uniblend.utils.WorldUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import java.util.Arrays;
import java.util.List;

public class Hoglibor extends UMob{

    @Property("controller")
    private ActionController controller;
    @Property("bossbar")
    private BossBar bossBar;
    @Property("explode-sound")
    private SoundInstance explodeSound = new SoundInstance(Sound.ENTITY_GENERIC_EXPLODE,new ValueRange(1,1),new ValueRange(0.5f,0.6f));
    @Property("jump-sound")
    private SoundInstance jumpSound = new SoundInstance(Sound.ENTITY_PIGLIN_ANGRY,1,0.5f);
    private Vector dashVelocity = new Vector(0,0,0);

    @Override
    public void onCreate() {
        super.onCreate();
        controller.owner(this);
    }

    @Override
    public void onSetup() {
        super.onSetup();
        setInvisible();
        setupModel("count_hoglibor");
    }


    @Override
    public void onTracked(Player e) {
        bossBar.addViewer(e.getPlayer());
    }

    @Override
    public void onUntracked(Player e) {
        bossBar.removeViewer(e.getPlayer());
    }
    @Override
    public void onSetupAI(AITaskSystem ai) {
        ai.senseForEntities(EntityType.PLAYER);
        ai.registerTask(new AITaskFindTarget(0,EntityType.PLAYER).layer(0));
        ai.registerTask(new AITask(1).setGoal(new RandomStrollGoal(NMSUtils.convert(getHandle()),0.4f)).layer(1));
        ai.registerTask(new TaskAttack(2,0.7f,7).layer(2));
        ai.registerTask(new TaskJumpOnTarget(3).layer(3));
    }

    @Override
    public void onUpdateAlive(long globalTicks) {
        super.onUpdateAlive(globalTicks);
        bossBar.progress(getHealth()/getMaxHealth());
    }

    @Override
    public void onHurt(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL && controller.is("attack_jump")) {
            e.setCancelled(true);
        }else{
            super.onHurt(e);
        }
    }

    @Override
    public void onActionBegin(Action state) {
        if (state.is("attack")){
            playAnimation("attack", 0.1f, 0.25f, 2, false);
        }else
        if (state.is("attack_strong")){
            playAnimation("attack_strong", 0.1f, 0.25f, 1, false);
        }else
        if (state.is("attack_jump")){
            playAnimation("attack_jump", 0.1f, 0.25f, 1, false);
            jumpSound.play(getLocation());
        }else
        if (state.is("attack_dash")){
            playAnimation("attack_dash", 0.1f, 0.25f, 1, false);
            jumpSound.play(getLocation());
        }
    }

    @Override
    public void onActionTick(Action state, int ticks) {
        if (state.is("attack") && (ticks == 10 || ticks == 5)){
            if (hasTarget()){
                if (distanceToTarget() < 8){
                    getTarget().setNoDamageTicks(0);
                    EntityUtils.damageEntity(getHandle(),getTarget(),false);
                }
            }
        }else
        if (state.is("attack_strong") && ticks == 5){
            if (hasTarget()){
                if (distanceToTarget() < 8){
                    EntityUtils.damageEntity(getHandle(),getTarget(),4,true);
                }
            }
        }else
        if (state.is("attack_jump")){
            if (ticks > 40) {
                if (hasTarget()) {
                    dashVelocity = MathUtils.direction(getLocation(), getTarget().getEyeLocation().add(0, 2, 0));
                }
            }
            if (ticks > 10 && ticks < 40){
                UParticleEffect.play(Particle.CAMPFIRE_COSY_SMOKE,getLocation(),3,0.5f,0.5f,0.5f,0,0,0,false,true);
            }
            if (ticks == 40){
                setVelocity(new Vector(dashVelocity.getX()*1.5f,1,dashVelocity.getZ()*1.5f));
            }
            if (ticks == 10){
                explode();
            }
            float rotation = (float) Math.toDegrees(Math.atan2(-dashVelocity.getX(),dashVelocity.getZ()));
            setRotation(rotation,0);
        }else
        if (state.is("attack_dash")){
            UParticleEffect.play(Particle.CAMPFIRE_COSY_SMOKE, getLocation(), 1, 0.5f, 0.5f, 0.5f, 0, 0, 0, false, true);
            if (ticks == 20) {
                if (hasTarget()) {
                    dashVelocity = MathUtils.direction(getLocation(), getTarget().getLocation());
                    setVelocity(new Vector(dashVelocity.getX() * 2, 0.25f, dashVelocity.getZ() * 2));
                }
            }
            for (Entity nearbyEntity : getLocation().getNearbyEntities(2, 2, 2)) {
                if (nearbyEntity instanceof Player player) {
                    player.damage(10);
                }
            }
            float rotation = (float) Math.toDegrees(Math.atan2(-dashVelocity.getX(), dashVelocity.getZ()));
            setRotation(rotation, 0);
        }
    }

    public void explode(){
        if (getHandle().isOnGround()) {
            for (Entity nearbyEntity : getLocation().getNearbyEntities(2, 2, 2)) {
                if (nearbyEntity instanceof Player player) {
                    player.damage(10);
                    player.setVelocity(new Vector(RandomUtils.randomFloat(-0.5f, 0.5f), 1, RandomUtils.randomFloat(-0.5f, 0.5f)));
                }
            }
            UParticleEffect.play(Particle.EXPLOSION_HUGE, getLocation(), 1, 0.5f, 0.5f, 0.5f, 0, 0, 0, true, true);
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    WorldUtils.spawnNaturalBlockParticle(getLocation().add(i, 0, j), getLocation().add(i, -1, j), 10);
                }
            }
            explodeSound.play(getLocation());
        }
    }

    @Override
    public List<ActionController> getControllers() {
        return Arrays.asList(controller);
    }


    class TaskAttack extends AITaskAttack{

        public TaskAttack(int id, float speed, float attackDistance) {
            super(id, speed, attackDistance, 25, (target)->{
                if (RandomUtils.doWithChance(50)) {
                    controller.playAction("attack");
                }else{
                    controller.playAction("attack_strong");
                }
            });
        }

        @Override
        public boolean canStart() {
            return super.canStart() && controller.isNoAction();
        }

        @Override
        public boolean canContinue() {
            return super.canContinue() && controller.isNoAction();
        }
    }

    class TaskJumpOnTarget extends AITaskTimer {

        public TaskJumpOnTarget(int id) {
            super(id, 100);
        }

        @Override
        public void onRun() {
            getTaskSystem().stopTask(1);
            if (controller.isNoAction()) {
                if (RandomUtils.doWithChance(50)) {
                    controller.playAction("attack_jump");
                }else{
                    controller.playAction("attack_dash");
                }
            }
        }

        @Override
        public boolean canStart() {
            return getHandle().isValid() && hasTarget();
        }

        @Override
        public boolean canContinue() {
            return getHandle().isValid() && hasTarget();
        }
    }
}
