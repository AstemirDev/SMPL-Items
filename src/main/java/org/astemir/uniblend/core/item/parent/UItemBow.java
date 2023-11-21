package org.astemir.uniblend.core.item.parent;


import com.google.gson.JsonObject;
import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class UItemBow extends UItem {
    @Property("bow-damage")
    private double bowDamage = -1.0;
    @Property("bow-damage-modifier")
    private double bowDamageModifier = 1.0;
    @Property("bow-crit-modifier")
    private double bowCritModifier = 1.0;
    @Property("bow-accuracy")
    private double bowAccuracy = 1.0;
    @Property("bow-force-modifier")
    private double bowForceModifier = 1.0;
    @Property("bow-scatter")
    private double bowScatterModifier = 0.0;
    @Property("bow-arrow-piercing")
    private int bowArrowPiercing = 0;
    @Property("bow-arrow-count")
    private int bowArrowCount = 1;
    @Property("is-crossbow")
    private boolean isCrossbow = false;
    @Override
    public EventExecutionResult onShoot(EntityShootBowEvent e) {
        boolean cancel = false;
        for (int i = 0;i<bowArrowCount;i++) {
            Entity projectile = createProjectile(e.getEntity(), e.getBow(), e.getConsumable(), e.getForce(), e.getProjectile());
            projectile.setMetadata(getNameKey(), new FixedMetadataValue(UniblendCorePlugin.getPlugin(), true));
            if (projectile instanceof Arrow arrow) {
                arrow.setPierceLevel(bowArrowPiercing);
            }
            e.setProjectile(projectile);
            e.setConsumeItem(consumeItem(e.getConsumable()));
            if (!shoot(e.getEntity(), e.getBow(), e.getHand(), e.getForce())){
                cancel = true;
            }
        }
        if (bowArrowCount <= 0 || cancel) {
            return EventExecutionResult.CANCEL;
        }
        return super.onShoot(e);
    }

    @Override
    public EventExecutionResult onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager().hasMetadata(getNameKey())) {
            e.setDamage(getDamage((float) e.getDamage(),e.isCritical()));
            return EventExecutionResult.from(!projectileDamageEntity(e.getDamager(),e.getEntity()));
        }
        return super.onEntityDamageByEntity(e);
    }

    public float getDamage(float defaultDamage,boolean isCritical){
        float res = defaultDamage;
        if (bowDamage != -1){
            res =  (float)bowDamage;
        }
        res *= bowDamageModifier;
        if (isCritical){
            res *= bowCritModifier;
        }
        return res;
    }

    public boolean shoot(LivingEntity shooter, ItemStack bow, EquipmentSlot hand, float force){
        return true;
    }

    public boolean projectileDamageEntity(Entity projectile,Entity damaged){
        return true;
    }

    public Entity createProjectile(LivingEntity shooter, ItemStack bowItem, ItemStack arrowItem, float force, Entity arrowProjectile){
        if (hasAnyProperty("bow-accuracy","bow-force-modifier","bow-scatter","bow-arrow-count")) {
            return EntityUtils.shootArrow(shooter, bowItem, (float)bowAccuracy, force* (float)bowForceModifier,  (float)bowScatterModifier, isCrossbow);
        }
        return arrowProjectile;
    }

    public boolean consumeItem(ItemStack consumable){
        return true;
    }
}
