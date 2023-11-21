package org.astemir.uniblend.core.entity.utils;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.astemir.uniblend.utils.NMSUtils;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EntityUtils {


    public static void doToNearbyLiving(Entity entity, float radius, Consumer<LivingEntity> onFound){
        for (LivingEntity nearbyEntity : entity.getLocation().getNearbyLivingEntities(radius, radius, radius)) {
            if (!entity.getUniqueId().equals(nearbyEntity.getUniqueId())) {
                onFound.accept(nearbyEntity);
            }
        }
    }


    public static void doToNearby(Entity entity, float radius, Consumer<Entity> onFound){
        for (Entity nearbyEntity : entity.getLocation().getNearbyEntities(radius, radius, radius)) {
            if (!entity.getUniqueId().equals(nearbyEntity.getUniqueId())) {
                onFound.accept(nearbyEntity);
            }
        }
    }

    public static void doToNearby(Location location, float radius, Consumer<Entity> onFound){
        for (Entity nearbyEntity : location.getNearbyEntities(radius, radius, radius)) {
            onFound.accept(nearbyEntity);
        }
    }

    public static void damageEntity(LivingEntity damager, Entity target){
        damageEntity(damager,target,1,false);
    }

    public static void damageEntity(LivingEntity damager, Entity target, boolean breakShield){
        damageEntity(damager,target,1,breakShield);
    }

    public static void damageEntity(LivingEntity damager, Entity target, float multiplier){
        damageEntity(damager,target,multiplier,false);
    }

    public static void damageEntity(LivingEntity damager, Entity target, float multiplier, boolean breakShield){
        net.minecraft.world.entity.LivingEntity nmsDamager = NMSUtils.convert(damager);
        net.minecraft.world.entity.Entity nmsTarget = NMSUtils.convert(target);
        float f = (float)nmsDamager.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float)nmsDamager.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (nmsTarget instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
            f += EnchantmentHelper.getDamageBonus(nmsDamager.getMainHandItem(), livingEntity.getMobType());
            f1 += (float)EnchantmentHelper.getKnockbackBonus(nmsDamager);
        }
        int i = EnchantmentHelper.getFireAspect(nmsDamager);
        if (i > 0) {
            nmsTarget.setSecondsOnFire(i * 4);
        }
        boolean flag = nmsTarget.hurt(nmsTarget.damageSources().mobAttack(nmsDamager), f*multiplier);
        if (nmsTarget instanceof net.minecraft.world.entity.player.Player) {
            net.minecraft.world.entity.player.Player player = (net.minecraft.world.entity.player.Player)nmsTarget;
            net.minecraft.world.item.ItemStack mainHand = nmsDamager.getMainHandItem();
            net.minecraft.world.item.ItemStack usingItem = player.isUsingItem() ? player.getUseItem() : net.minecraft.world.item.ItemStack.EMPTY;
            if (((!mainHand.isEmpty() && mainHand.getItem() instanceof AxeItem) || breakShield) && usingItem.is(Items.SHIELD)) {
                player.getCooldowns().addCooldown(Items.SHIELD, 100);
                player.stopUsingItem();
                nmsDamager.level().broadcastEntityEvent(player, (byte)30);
            }
        }
        if (flag) {
            if (f1 > 0.0F && nmsTarget instanceof net.minecraft.world.entity.LivingEntity) {
                ((net.minecraft.world.entity.LivingEntity)nmsTarget).knockback(f1 * 0.5F, Mth.sin(nmsDamager.getYRot() * ((float)Math.PI / 180F)), -Mth.cos(nmsDamager.getYRot() * ((float)Math.PI / 180F)));
                nmsDamager.setDeltaMovement(nmsDamager.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }
            nmsDamager.doEnchantDamageEffects(nmsDamager, nmsTarget);
            nmsDamager.setLastHurtMob(nmsTarget);
        }
    }

    public static Location getEntityDynamicLocation(Entity entity){
        return entity.getLocation().clone().add(0,entity.getHeight()/2,0).add(entity.getVelocity());
    }
    public static Location getEntityEyeLocation(LivingEntity entity){
        return entity.getEyeLocation().clone().add(0,- 0.10000000149011612D,0);
    }

    public static ItemStack getItemStackInUse(LivingEntity livingEntity){
        if (livingEntity instanceof Player player) {
            return player.getItemInUse();
        }else{
            net.minecraft.world.entity.LivingEntity nmsLivingEntity = NMSUtils.convert(livingEntity);
            return NMSUtils.convert(nmsLivingEntity.getUseItem());
        }
    }

    public static Map<EquipmentSlot,ItemStack> getEquipmentArmor(LivingEntity livingEntity){
        Map<EquipmentSlot,ItemStack> map = new HashMap<>();
        map.put(EquipmentSlot.HEAD,livingEntity.getEquipment().getItem(EquipmentSlot.HEAD));
        map.put(EquipmentSlot.CHEST,livingEntity.getEquipment().getItem(EquipmentSlot.CHEST));
        map.put(EquipmentSlot.LEGS,livingEntity.getEquipment().getItem(EquipmentSlot.LEGS));
        map.put(EquipmentSlot.FEET,livingEntity.getEquipment().getItem(EquipmentSlot.FEET));
        return map;
    }

    public static ItemStack getItemInMainHand(LivingEntity livingEntity){
        return getItemInSlot(livingEntity,EquipmentSlot.HAND);
    }

    public static ItemStack getItemInOffHand(LivingEntity livingEntity){
        return getItemInSlot(livingEntity,EquipmentSlot.OFF_HAND);
    }

    public static boolean isItemEquipped(LivingEntity entity,EquipmentSlot slot){
        return getItemInSlot(entity,slot) != null;
    }

    public static ItemStack getItemInSlot(LivingEntity entity, EquipmentSlot slot){
        if (entity != null) {
            return entity.getEquipment().getItem(slot);
        }
        return null;
    }

    public static Vector getEntityDirection(Entity entity){
        float rotationYaw = entity.getLocation().getYaw(), rotationPitch = entity.getLocation().getPitch();
        float vx = (float) ((float)-Math.sin(Math.toRadians(rotationYaw)) * Math.cos(Math.toRadians(rotationPitch)));
        float vz = (float) ((float)Math.cos(Math.toRadians(rotationYaw)) * Math.cos(Math.toRadians(rotationPitch)));
        float vy = (float)- Math.sin(Math.toRadians(rotationPitch));
        return new Vector(vx,vy,vz);
    }

    public static void dropItem(Entity entity,ItemStack itemStack){
        Item item = (Item)entity.getWorld().spawnEntity(entity.getLocation().clone().add(0,0.5f,0), EntityType.DROPPED_ITEM);
        item.setItemStack(itemStack);
    }

    public static void explodeFirework(Location loc, int power,FireworkEffect... effects){
        Firework firework = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(power);
        meta.addEffects(effects);
        firework.setFireworkMeta(meta);
        firework.detonate();
    }

    public static AbstractArrow shootArrow(Entity shooter, ItemStack stack, float accuracy, float power, float scatter, boolean crossbow){
        Level level = ((CraftEntity)shooter).getHandle().level();
        net.minecraft.world.item.ItemStack serverItemStack = CraftItemStack.asNMSCopy(stack);
        ArrowItem itemArrow = (ArrowItem) (serverItemStack.getItem() instanceof ArrowItem ? serverItemStack.getItem() : Items.ARROW);
        net.minecraft.world.entity.LivingEntity shooterLiving = NMSUtils.convert(shooter);
        net.minecraft.world.entity.projectile.AbstractArrow entityArrow = itemArrow.createArrow(level, serverItemStack, shooterLiving);
        if (shooter instanceof Player) {
            if (power >= 1.0f) {
                entityArrow.setCritArrow(true);
            }
            if (crossbow) {
                entityArrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
                entityArrow.setShotFromCrossbow(true);
                int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, serverItemStack);
                if (i > 0) {
                    entityArrow.setPierceLevel((byte) i);
                }
                entityArrow.shootFromRotation(shooterLiving, shooterLiving.getXRot(), shooterLiving.getYRot(), scatter, power * 3.0F, scatter);
            } else {
                int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, serverItemStack);
                if (k > 0) {
                    entityArrow.setBaseDamage(entityArrow.getBaseDamage() + (double) k * 0.5D + 0.5D);
                }

                int l = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, serverItemStack);
                if (l > 0) {
                    entityArrow.setKnockback(l);
                }

                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, serverItemStack) > 0) {
                    entityArrow.setSecondsOnFire(100);
                }
                entityArrow.shootFromRotation(shooterLiving, shooterLiving.getXRot(), shooterLiving.getYRot(), scatter, power * 3.0F, accuracy);
            }
        }else{
            if (shooterLiving instanceof net.minecraft.world.entity.Mob mob) {
                if (mob.getTarget() != null) {
                    double d0 = mob.getTarget().getX() - mob.getX();
                    double d1 = mob.getTarget().getY(0.3333333333333333) - entityArrow.getY();
                    double d2 = mob.getTarget().getZ() - mob.getZ();
                    double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                    if (!crossbow) {
                        entityArrow.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 3*power, (float) (14 - level.getDifficulty().getId() * 4)*accuracy);
                    }else{
                        double var11 = mob.getTarget().getY(0.3333333333333333) - entityArrow.getY() + d3 * 0.20000000298023224;
                        Vector3f var13 = getProjectileShotVector(mob, new Vec3(d0, var11, d2), accuracy);
                        entityArrow.shoot(var13.x(), var13.y(), var13.z(), 3*power, (float)(14 - level.getDifficulty().getId() * 4)*accuracy);
                    }
                }
            }
        }
        level.addFreshEntity(entityArrow);
        return NMSUtils.convert(entityArrow);
    }

    private static Vector3f getProjectileShotVector(net.minecraft.world.entity.LivingEntity var0, Vec3 var1, float var2) {
        Vector3f var3 = var1.toVector3f().normalize();
        Vector3f var4 = (new Vector3f(var3)).cross(new Vector3f(0.0F, 1.0F, 0.0F));
        if ((double)var4.lengthSquared() <= 1.0E-7) {
            Vec3 var5 = var0.getUpVector(1.0F);
            var4 = (new Vector3f(var3)).cross(var5.toVector3f());
        }

        Vector3f var5 = (new Vector3f(var3)).rotateAxis(1.5707964F, var4.x, var4.y, var4.z);
        return (new Vector3f(var3)).rotateAxis(var2 * 0.017453292F, var5.x, var5.y, var5.z);
    }

    public static Firework shootFirework(Entity shooter, ItemStack stack, float power, float accuracy){
        net.minecraft.world.entity.LivingEntity shooterLiving = NMSUtils.convert(shooter);
        Level level = shooterLiving.level();
        FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(level, NMSUtils.convert(stack), shooterLiving, shooterLiving.getX(), shooterLiving.getEyeY() - 0.15000000596046448D, shooterLiving.getZ(), true);
        fireworkRocketEntity.shootFromRotation(shooterLiving, shooterLiving.getXRot(), shooterLiving.getYRot(), accuracy, power * 1.6F, accuracy);
        level.addFreshEntity(fireworkRocketEntity);
        return (Firework) fireworkRocketEntity.getBukkitEntity();
    }
}
