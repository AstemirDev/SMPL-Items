package org.astemir.uniblend.core.entity.parent.builtin;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import dev.lone.itemsadder.api.CustomEntity;
import net.kyori.adventure.bossbar.BossBar;
import org.astemir.uniblend.core.entity.parent.UMob;
import org.astemir.uniblend.core.entity.action.Action;
import org.astemir.uniblend.core.entity.action.ActionController;
import org.astemir.uniblend.core.entity.ai.AITaskSystem;
import org.astemir.uniblend.core.entity.ai.tasks.AITask;
import org.astemir.uniblend.core.entity.ai.tasks.AITaskFindTarget;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.particle.UParticleBeam;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.misc.SoundInstance;
import org.astemir.uniblend.utils.MathUtils;
import org.astemir.uniblend.utils.NMSUtils;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.joml.Vector2d;

import java.util.Arrays;
import java.util.List;

public class DragonGuardian extends UMob {

    @Property("particle")
    private UParticleEffect particle = new UParticleEffect(Particle.FALLING_DUST).block(Material.PURPLE_CARPET).size(0.5f,0.5f,0.5f).count(10).distant().randomSpeed();
    @Property("particle-beam")
    private UParticleEffect particleBeam = new UParticleEffect(Particle.REDSTONE).color(Color.fromRGB(0,255,0),0.75f);
    @Property("dash-sound")
    private SoundInstance dashSound;
    @Property("shoot-sound")
    private SoundInstance shootSound;
    @Property("controller")
    private ActionController controller;
    @Property("bossbar")
    private BossBar bossBar;
    @Property("beam-damage")
    private float beamDamage = 4;
    @Property("dash-damage")
    private float dashDamage = 10f;
    @Property("wander-radius")
    private float wanderRadius = 10f;
    private Vector velocity = new Vector(0,0,0);

    @Override
    public void onCreate() {
        super.onCreate();
        controller.owner(this);
    }

    @Override
    public void onSetup() {
        super.onSetup();
        addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,-1,1,false,false,false));
        setInvisible();
        setNoGravity();
        setNoPhysics();
        setupModel("dragon_guardian");
    }

    @Override
    public void onSetupAI(AITaskSystem ai) {
        ai.senseForEntities(EntityType.PLAYER);
        ai.registerTask(new AITaskFindTarget(0,EntityType.PLAYER).layer(0));
        ai.registerTask(new TaskRandomStroll(1).layer(1));
        ai.registerTask(new TaskWander(2).layer(1).interrupts(1));
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
    public void onUpdateAlive(long globalTicks) {
        super.onUpdateAlive(globalTicks);
        if (globalTicks % 20 == 0) {
            if (isInGround()) {
            } else {
            }
        }
        bossBar.progress(getHealth()/getMaxHealth());
        particle.play(getLocation());
        velocity = MathUtils.lerp(velocity,new Vector(0,0,0),0.025f);
        setVelocity(velocity);
    }


    @Override
    public void onHurt(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL || e.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL){
            e.setCancelled(true);
        }
        super.onHurt(e);
    }



    @Override
    public void remove() {
        super.remove();
    }

    public void lightning(Entity entity){
        Location loc = getHandle().getLocation().clone().add(0, 1,0).add(velocity);
        Location livingLoc = entity.getLocation().clone().add(0, entity.getHeight(), 0);
        Vector direction = livingLoc.clone().add(RandomUtils.randomFloat(-0.1f, 0.1f), RandomUtils.randomFloat(-0.25f, 0.25f), RandomUtils.randomFloat(-0.1f, 0.1f)).subtract(loc.clone().add(RandomUtils.randomFloat(-0.1f, 0.1f), RandomUtils.randomFloat(-0.25f, 0.25f), RandomUtils.randomFloat(-0.1f, 0.1f))).toVector();
        UParticleBeam.sendObservableParticleLightning(particleBeam, loc, direction, 2, (target) -> {
            if (!target.getUniqueId().equals(getHandle().getUniqueId())) {
                if (target instanceof LivingEntity livingEntity) {
                    livingEntity.damage(beamDamage, getHandle());
                }
                return true;
            }
            return false;
        }, 8, 4, false);
    }

    @Override
    public void onActionBegin(Action state) {
        super.onActionBegin(state);
        if (state.is("roll")){
            playAnimation("roll",0.1f,0.25f,1f,false);
        }
    }

    @Override
    public void onActionEnd(Action state) {
        super.onActionEnd(state);
        if (state.is("roll")){
            stopAnimation("roll");
        }
    }

    private class TaskRandomStroll extends AITask{

        public TaskRandomStroll(int id) {
            super(id);
        }

        @Override
        public void onUpdate() {
            if (RandomUtils.doWithChance(5) && getTicks() % 40 > 20){
                velocity = new Vector(RandomUtils.randomFloat(-0.3f,0.3f),RandomUtils.randomFloat(-0.3f,0.3f),RandomUtils.randomFloat(-0.3f,0.3f));
            }
            Vector2d rotation = MathUtils.getRotation(velocity);
            setRotation((float) rotation.x, (float) rotation.y);
        }
    }


    private class TaskWander extends AITask{

        public TaskWander(int id) {
            super(id);
        }

        @Override
        public void onUpdate() {
            Vector directionToPlayer = MathUtils.direction(getLocation().clone().add(velocity),getTarget().getLocation());
            if (getTicks() % 200 == 0){
                if (RandomUtils.doWithChance(50)){
                    velocity = new Vector(0,0,0);
                    controller.playAction("dashing");
                }else
                if (RandomUtils.doWithChance(50)) {
                    velocity = new Vector(0,0,0);
                    controller.playAction("roll");
                }
            }
            if (controller.is("wander")) {
                Location targetLoc = getTarget().getLocation().clone().add(Math.cos(getTicks()/5)*wanderRadius,4+Math.sin(getTicks()/4)*2,Math.sin(getTicks()/5)*wanderRadius);
                velocity = MathUtils.direction(getLocation(), targetLoc);
                Vector2d rotation = MathUtils.getRotation(directionToPlayer);
                setRotation((float) rotation.x, (float) rotation.y);
                if (getTicks() % 100 > 60) {
                    lightning(getTarget());
                    shootSound.play(getLocation());
                }
            }else
            if (controller.is("roll")){
                if (getTicks() % 10 == 0){
                    ShulkerBullet bullet = (ShulkerBullet) getLocation().getWorld().spawnEntity(getLocation().clone().add(Math.cos(getTicks()/4)*4,Math.sin(getTicks()/4)*4,Math.sin(getTicks()/4)*4),EntityType.SHULKER_BULLET);
                    bullet.setShooter(getHandle());
                    bullet.setTarget(getTarget());
                }
            }else
            if (controller.is("dashing")){
                if (getTicks() % 20 == 0){
                    Location targetLoc = getTarget().getLocation().clone();
                    velocity = MathUtils.direction(getLocation(), targetLoc).multiply(1.5f);
                    playAnimation("dash",0.1f,0.25f,1,false);
                    dashSound.play(getLocation());
                }
                EntityUtils.doToNearbyLiving(getHandle(),2,(entity)->entity.damage(dashDamage));
                Vector2d rotation = MathUtils.getRotation(directionToPlayer);
                setRotation((float) rotation.x, (float) rotation.y);
            }else
            if (controller.isNoAction()){
                controller.playAction("wander");
            }
        }

        @Override
        public boolean canStart() {
            return super.canStart() && hasTarget();
        }

        @Override
        public boolean canContinue() {
            return super.canContinue() && hasTarget();
        }
    }

    @Override
    public List<ActionController> getControllers() {
        return Arrays.asList(controller);
    }
}
