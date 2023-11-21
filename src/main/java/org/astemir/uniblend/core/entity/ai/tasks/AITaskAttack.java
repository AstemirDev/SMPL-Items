package org.astemir.uniblend.core.entity.ai.tasks;


import org.astemir.uniblend.core.entity.ai.triggers.TaskMalus;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class AITaskAttack extends AITask{
    private float speed;
    private float attackDistance;
    private int attackDelay;
    private AttackFunction attackFunction;
    private boolean isShouldSeeTarget = true;
    private int attackTicks = 0;

    public AITaskAttack(int id,float speed,float attackDistance,int attackDelay,AttackFunction attackFunction) {
        super(id);
        this.speed = speed;
        this.attackDistance = attackDistance;
        this.attackDelay = attackDelay;
        this.attackFunction = attackFunction;
        malus(TaskMalus.ATTACK,TaskMalus.MOVE,TaskMalus.MOVE);
    }

    public AITaskAttack(int id,float speed) {
        this(id,speed,2,20,null);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && getEntity().hasTarget();
    }

    @Override
    public boolean canContinue() {
        return super.canStart() && getEntity().hasTarget();
    }

    public boolean canContinueAttack(){
        return getEntity().isValid();
    }

    public boolean canUseAttack(){
        if (attackDelay != 0) {
            return (attackTicks+1) % attackDelay == 0;
        }else{
            return true;
        }
    }

    @Override
    public void onStart() {
        attackTicks = 0;
    }

    @Override
    public void onStop() {
        attackTicks = 0;
        getEntity().getPathfinder().stopPathfinding();
    }

    @Override
    public void onUpdate() {
        Mob owner = getEntity().getHandle();
        LivingEntity target = getEntity().getTarget();
        if (target instanceof Player player){
            if (player.getGameMode() == GameMode.SPECTATOR  || player.getGameMode() == GameMode.CREATIVE){
                stop();
                return;
            }
        }
        if (canContinueAttack()) {
            if (isShouldSeeTarget){
                boolean canSee = owner.hasLineOfSight(target);
                if (!canSee){
                    stop();
                    return;
                }
            }
            if (owner.getLocation().distanceSquared(target.getLocation()) > attackDistance) {
                ai().lookEntity(this,target, 10, 10f);
                if (getTicks() % 10 == 0) {
                    ai().moveEntity(this,target, speed);
                }
            } else {
                if (canUseAttack()) {
                    if (attackFunction != null) {
                        attackFunction.attack(target);
                        attackTicks = 0;
                    } else {
                        attack();
                    }
                }
            }
        }
        attackTicks++;
    }

    public void attack(){
        if (canUseAttack()) {
            ai().attack(this, getEntity().getTarget(), attackDistance);
        }
    }

    public AITaskAttack shouldSeeTarget(boolean b){
        isShouldSeeTarget = b;
        return this;
    }

    public void setAttackFunction(AttackFunction attackFunction) {
        this.attackFunction = attackFunction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getAttackDistance() {
        return attackDistance;
    }

    public void setAttackDistance(float attackDistance) {
        this.attackDistance = attackDistance;
    }

    public int getAttackDelay() {
        return attackDelay;
    }

    public void setAttackDelay(int attackDelay) {
        this.attackDelay = attackDelay;
    }

    public interface AttackFunction{
        void attack(LivingEntity target);
    }
}
