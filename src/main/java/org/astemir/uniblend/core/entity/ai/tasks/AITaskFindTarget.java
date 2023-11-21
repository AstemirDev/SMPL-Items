package org.astemir.uniblend.core.entity.ai.tasks;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import java.util.List;

public class AITaskFindTarget extends AITask{

    private EntityType[] types;
    public AITaskFindTarget(int id,EntityType... types) {
        super(id);
        this.types = types;
    }

    @Override
    public void onUpdate() {
        LivingEntity target = findTarget();
        getEntity().setTarget(target);
    }

    private LivingEntity findTarget(){
        List<Entity> sensed = getTaskSystem().getSensedEntities();
        for (Entity entity : sensed) {
            if (entity instanceof LivingEntity livingEntity && getEntity().canTarget(entity)){
                if (isTargetableEntity(entity)) {
                    return livingEntity;
                }
            }
        }
        return null;
    }

    private boolean isTargetableEntity(Entity entity){
        for (EntityType type : types) {
            if (entity.getType() == type){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canStart() {
        return getEntity().isValid() && (!getEntity().hasTarget() || !getEntity().canAttack(getEntity().getTarget()));
    }

    @Override
    public boolean canContinue() {
        return getEntity().isValid() && !getEntity().hasTarget() || !getEntity().canAttack(getEntity().getTarget());
    }
}