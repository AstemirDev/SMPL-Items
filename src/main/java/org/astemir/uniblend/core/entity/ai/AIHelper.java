package org.astemir.uniblend.core.entity.ai;



import org.astemir.uniblend.core.entity.parent.UMob;
import org.astemir.uniblend.core.entity.ai.tasks.AITask;
import org.astemir.uniblend.core.entity.ai.triggers.TaskMalus;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;


public class AIHelper {

    private AITaskSystem aiTaskSystem;

    public AIHelper(AITaskSystem aiTaskSystem) {
        this.aiTaskSystem = aiTaskSystem;
    }

    public boolean moveEntity(AITask request, Location location, float speed){
        if (!isConflictsAt(request, TaskMalus.MOVE)) {
            getEntity().getPathfinder().moveTo(location,speed);
            return true;
        }
        return false;
    }

    public boolean moveEntity(AITask request, Entity entity, float speed){
        if (!isConflictsAt(request, TaskMalus.MOVE)) {
            getEntity().getPathfinder().moveTo(entity.getLocation(),speed);
            return true;
        }
        return false;
    }

    public boolean lookEntity(AITask request,Location location, float xSpeed,float ySpeed){
        if (!isConflictsAt(request,TaskMalus.LOOK)) {
            getEntity().getHandle().lookAt(location, xSpeed, ySpeed);
            return true;
        }
        return false;
    }

    public boolean lookEntity(AITask request,float x,float y,float z, float xSpeed,float ySpeed){
        if (!isConflictsAt(request,TaskMalus.LOOK)) {
            getEntity().getHandle().lookAt(x,y,z, xSpeed, ySpeed);
            return true;
        }
        return false;
    }

    public boolean lookEntity(AITask request,Entity entity, float xSpeed,float ySpeed){
        if (!isConflictsAt(request,TaskMalus.LOOK)) {
            getEntity().getHandle().lookAt(entity, xSpeed, ySpeed);
            return true;
        }
        return false;
    }

    public boolean attack(AITask request, LivingEntity target, float distance){
        if (!isConflictsAt(request,TaskMalus.ATTACK)){
            Mob mob = getEntity().getHandle();
            if (mob != null && target != null && mob.isValid() && target.isValid() && mob.getLocation().distanceSquared(target.getLocation()) <= distance) {
                mob.swingHand(EquipmentSlot.HAND);
                mob.attack(target);
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean jump(AITask request){
        if (!isConflictsAt(request,TaskMalus.JUMP)){
            getEntity().getHandle().setJumping(true);
            return true;
        }
        return false;
    }

    public boolean isConflictsAt(AITask task,TaskMalus malus){
        for (AITask runningTask : aiTaskSystem.getRunningTasks()) {
            if (runningTask.getId() != task.getId()){
                return runningTask.hasMalus(malus);
            }
        }
        return false;
    }

    public UMob getEntity(){
        return aiTaskSystem.getEntity();
    }
}
