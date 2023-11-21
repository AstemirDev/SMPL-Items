package org.astemir.uniblend.core.projectile.builtin;

import org.astemir.uniblend.core.display.RenderProperty;
import org.astemir.uniblend.core.display.RenderSettings;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.core.display.URenderer;
import org.astemir.uniblend.core.projectile.UProjectile;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.misc.ItemComponent;
import org.astemir.uniblend.misc.SoundInstance;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.utils.MathUtils;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BloodPact extends UProjectile {

    @Property("sound")
    private SoundInstance soundInstance = new SoundInstance("uniblend.items.bloodmourner",1,1);
    @Property("sound")
    private UParticleEffect particle = new UParticleEffect(Particle.FALLING_DUST).block(Material.PURPLE_CARPET).size(0.1f,0.1f,0.1f).count(1).distant().randomSpeed();
    @Property("self-effect")
    private PotionEffect projectileEffect = new PotionEffect(PotionEffectType.REGENERATION,80,1,false,false,false);
    @Property("model-stack-0")
    private ItemComponent modelStack0 = ItemComponent.of(ItemUtils.itemWithModel(Material.NETHERITE_SWORD,1450051));
    @Property("model-stack-1")
    private ItemComponent modelStack1 = ItemComponent.of(ItemUtils.itemWithModel(Material.NETHERITE_SWORD,1450052));
    @Property("homing-radius")
    private float projectileHomingRadius = 8;
    @Property("block-transform-chance")
    private int blockTransformChance = 10;
    @Property("damage")
    private float damage = 10;
    @Property("speed-factor")
    private float speedFactor = 0.35f;
    private URenderer middle;
    private URenderer orb;

    @Override
    public void onCreate() {
        super.onCreate();
        filter((entity)->{
            if (entity instanceof LivingEntity){
                if (getShooter() != null) {
                    if (entity.getUniqueId().equals(getShooter().getUniqueId())) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void shoot(Entity shooter, Location location) {
        super.shoot(shooter, location);
        middle = new URenderer(location,new RenderSettings(new RenderProperty.ItemProperty(modelStack0.getItemStack().get())).brightness(15));
        orb = new URenderer(location,new RenderSettings(new RenderProperty.ItemProperty(modelStack1.getItemStack().get())).brightness(15));
        middle.setInterpolationDuration(40);
        orb.setInterpolationDuration(20);
    }

    @Override
    public void onHitBlock(Block block, BlockFace face) {
        super.onHitBlock(block, face);
        if (block.getType() == Material.OBSIDIAN && RandomUtils.doWithChance(blockTransformChance)){
            block.setType(Material.CRYING_OBSIDIAN);
        }
    }

    @Override
    public void onHitEntity(Entity entity) {
        super.onHitEntity(entity);
        if (getShooter() != null){
            ((LivingEntity)getShooter()).addPotionEffect(projectileEffect);
        }
        ((LivingEntity)entity).damage(damage,getShooter());
        ((LivingEntity)entity).setNoDamageTicks(0);
        entity.setFireTicks(100);
    }

    @Override
    public void onUpdate(long ticks) {
        Quaternionf rotation = new Quaternionf();
        rotation = rotation.lookAlong(getVelocity().toVector3f(),new Vector3f(0,1,0));
        Vector3f orbOffset = new Vector3f(0,(float) Math.sin(ticks/4f)/8f,0);
        middle.settings().rotation(new Quaternionf(rotation).rotateLocalY((float) Math.toRadians(ticks)*10));
        middle.setPosition(getPosition());
        orb.setPosition(getPosition());
        orb.settings().rotation(rotation);
        orb.settings().translation(orbOffset);
        particle.play(getLocation().clone().add(getVelocity()).add(orbOffset.x,orbOffset.y,orbOffset.z));
        LivingEntity target = null;
        for (Entity nearbyEntity : getLocation().getNearbyEntities(projectileHomingRadius, projectileHomingRadius, projectileHomingRadius)) {
            if (nearbyEntity instanceof LivingEntity livingEntity) {
                if (getShooter() != null) {
                    if (!nearbyEntity.getUniqueId().equals(getShooter().getUniqueId())) {
                        target = livingEntity;
                    }
                }else{
                    target = livingEntity;
                }
            }
        }
        if (target != null) {
            Vector velocity = getVelocity();
            Vector newVec = target.getLocation().add(0,target.getHeight()/2f,0).subtract(getLocation()).toVector().normalize().multiply(3);
            setVelocity(MathUtils.lerp(velocity, newVec, speedFactor));
        }
    }

    @Override
    public void onDie() {
        new UParticleEffect(Particle.BLOCK_DUST).block(Material.REDSTONE_BLOCK).renderTimes(5).size(0.35f,0.35f,0.35f).speed(0.2f,1f,0.2f).count(15).distant().randomSpeed().play(getLocation());
        getLocation().getWorld().playSound(getLocation(), Sound.ENTITY_GENERIC_EXPLODE,1, RandomUtils.randomFloat(1.75f,2));
        soundInstance.play(getLocation(),0.25f,RandomUtils.randomFloat(0.75f,0.85f));
        middle.remove();
        orb.remove();
    }
}
