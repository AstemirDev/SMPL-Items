package org.astemir.uniblend.core.item.parent.builtin.melee;

import org.astemir.uniblend.core.cooldown.UCooldownHandler;
import org.astemir.uniblend.core.item.parent.UItemSword;
import org.astemir.uniblend.core.particle.UParticleBeam;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Vector3d;

public class WithersBane extends UItemSword {

    @Property("max-targets")
    private int maxTargets = 6;
    @Property("cooldown")
    private int cooldown = 20;
    @Property("damage")
    private double damage = 4.0;
    @Property("radius")
    private Vector3d radius = new Vector3d(10,10,10);

    @Override
    public EventExecutionResult onAttackEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Monster){
            if (UCooldownHandler.doWithCooldown(e.getDamager(),"withersbane",cooldown)){
                int i = 0;
                if (((Monster) e.getEntity()).getCategory() == EntityCategory.UNDEAD) {
                    for (Entity livingEntity : e.getEntity().getNearbyEntities(radius.x, radius.y, radius.z)) {
                        if (livingEntity instanceof Monster) {
                            if (i < maxTargets) {
                                Monster monster = (Monster) livingEntity;
                                if (monster.getCategory() == EntityCategory.UNDEAD) {
                                    Location startLoc = e.getEntity().getLocation().clone().add(new Vector(0, 1, 0));
                                    Location monsterLoc = monster.getLocation().clone().add(new Vector(0, 1, 0));
                                    Vector dir = monsterLoc.toVector().add(startLoc.toVector().multiply(-1)).normalize();
                                    int distance = (int) startLoc.distance(monsterLoc) + 1;
                                    UParticleBeam.sendDamageableBeam(Particle.END_ROD, startLoc, dir, distance, (ent) -> {
                                        if (ent instanceof Monster) {
                                            if (ent.getCategory() == EntityCategory.UNDEAD) {
                                                ent.damage(damage, e.getDamager());
                                                return true;
                                            }
                                        }
                                        return false;
                                    }, false);
                                    UParticleBeam.sendBeam(Particle.CLOUD, startLoc, dir, distance, false);
                                    i++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.onAttackEntity(e);
    }

    @Override
    public ItemStack toItemStack() {
        ItemStack stack = super.toItemStack();
        stack.addUnsafeEnchantment(Enchantment.DURABILITY,4);
        return stack;
    }
}

