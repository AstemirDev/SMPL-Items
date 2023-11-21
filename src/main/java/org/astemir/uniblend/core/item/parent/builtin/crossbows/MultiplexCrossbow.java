package org.astemir.uniblend.core.item.parent.builtin.crossbows;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.entity.EntityTaskHandler;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

public class MultiplexCrossbow extends UItem {

    @Property("arrow-count")
    private int arrowCount = 3;

    @Override
    public EventExecutionResult onShoot(EntityShootBowEvent e) {
        Entity projectile = e.getProjectile();
        ItemUtils.damageItem(e.getEntity(),e.getBow(),1);
        e.getEntity().getWorld().playSound(e.getEntity().getLocation(),Sound.ITEM_CROSSBOW_SHOOT,1,1.5f);
        e.getEntity().getWorld().playSound(e.getEntity().getLocation(),Sound.ITEM_CROSSBOW_SHOOT,1,0.5f);
        for (int i = 0;i<4;i++) {
            e.getEntity().getWorld().spawnParticle(Particle.LAVA, e.getEntity().getLocation().getX()+ RandomUtils.randomFloat(-0.5f,0.5f), e.getEntity().getLocation().getY()+ 1f+RandomUtils.randomFloat(-0.5f,0.75f), e.getEntity().getLocation().getZ()+ RandomUtils.randomFloat(-0.5f,0.5f), 0, 0, 0, 0);
        }
        for (int i = 0;i<arrowCount;i++) {
            Vector randomVec = new Vector(RandomUtils.randomFloat(-0.5f, 0.5f), RandomUtils.randomFloat(-0.5f, 0.5f), RandomUtils.randomFloat(-0.5f, 0.5f));
            if (projectile instanceof AbstractArrow){
                AbstractArrow arrow = EntityUtils.shootArrow(e.getEntity(),e.getArrowItem(),1,e.getForce(),0,true);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                arrow.setVelocity(arrow.getVelocity().clone().add(randomVec));
                EntityTaskHandler.INSTANCE.add(new EntityTaskHandler.EntityRunnable(arrow){
                    @Override
                    public void run(Entity entity, long ticks) {
                        if (ticks % 20 == 0) {
                            entity.getWorld().spawnParticle(Particle.LAVA, entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), 0, 0, 0, 0);
                        }
                    }
                }).lifespan(100);
            }else
            if (projectile instanceof Firework){
                Firework firework = EntityUtils.shootFirework(e.getEntity(),e.getArrowItem(),e.getForce(),1);
                firework.setVelocity(firework.getVelocity().clone().add(randomVec));
            }
        }
        return EventExecutionResult.CANCEL;
    }



}
