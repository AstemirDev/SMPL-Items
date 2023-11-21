package org.astemir.uniblend.core.entity.ai.tasks;



import net.minecraft.world.entity.ai.goal.Goal;
import org.astemir.uniblend.core.entity.parent.UEntity;
import org.astemir.uniblend.core.entity.parent.UMob;
import org.astemir.uniblend.core.entity.ai.AIHelper;
import org.astemir.uniblend.core.entity.ai.AITaskSystem;
import org.astemir.uniblend.core.entity.ai.triggers.TaskExecution;
import org.astemir.uniblend.core.entity.ai.triggers.TaskMalus;
import org.astemir.uniblend.core.entity.ai.triggers.TaskTrigger;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AITask implements UniblendAITask{
    private AITaskSystem system;
    private TaskTrigger taskTrigger = TaskTrigger.AUTO_ENABLE;
    private TaskExecution execution = TaskExecution.INFINITE;
    private TaskMalus[] malus = new TaskMalus[]{};
    private int id = 0;
    private int priority = 0;
    private int ticks = 0;
    private int layer = 0;
    private boolean running = false;
    private int[] canInterrupt = new int[]{};
    private Goal goal;

    public AITask(int id) {
        this.id = id;
    }


    public boolean canStart(){
        if (goal != null){
            return goal.canUse() && getEntity().isValid();
        }
        return getEntity().isValid();
    }

    public boolean canContinue(){
        if (goal != null){
            return getEntity().isValid() && goal.canContinueToUse();
        }
        return getEntity().isValid();
    }

    public void start(){
        running = true;
        ticks = 0;
        if (goal != null){
            goal.start();
        }
        onStart();
    }

    public void update(){
        ticks++;
        if (goal != null){
            goal.tick();
        }
        onUpdate();
    }

    public void stop(){
        running = false;
        ticks = 0;
        if (goal != null){
            goal.stop();
        }
        onStop();
    }

    public AITask layer(int layer){
        this.layer = layer;
        return this;
    }

    public AITask interrupts(int... id){
        this.canInterrupt = id;
        return this;
    }

    public AITask malus(TaskMalus... malus){
        this.malus = malus;
        return this;
    }

    public AITask priority(int priority){
        this.priority = priority;
        return this;
    }

    public AITask trigger(TaskTrigger trigger){
        this.taskTrigger = trigger;
        return this;
    }

    public AITask execution(TaskExecution execution){
        this.execution = execution;
        return this;
    }

    public AITask setGoal(Goal goal){
        this.goal = goal;
        return this;
    }

    public void register(AITaskSystem system) {
        this.system = system;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isConflictingWithOtherTask(){
        for (AITask runningTask : system.getRunningTasks()) {
            if (!isCanInterrupt(runningTask.getId()) && runningTask.getId() != getId()) {
                return getLayer() == runningTask.getLayer();
            }
        }
        return false;
    }

    public boolean isCanInterrupt(int id){
        for (int interruptId : getCanInterrupt()) {
            if (interruptId == id){
                return true;
            }
        }
        return false;
    }

    public int getPriority() {
        return priority;
    }

    public int getTicks() {
        return ticks;
    }

    public int getId() {
        return id;
    }

    public int getLayer() {
        return layer;
    }

    public int[] getCanInterrupt() {
        return canInterrupt;
    }

    public boolean hasMalus(TaskMalus malus){
        for (TaskMalus taskMalus : getMalus()) {
            if (taskMalus == malus){
                return true;
            }
        }
        return false;
    }

    public TaskMalus[] getMalus() {
        return malus;
    }

    public UMob getEntity(){
        return getTaskSystem().getEntity();
    }

    public TaskExecution getExecution() {
        return execution;
    }

    public TaskTrigger getTaskTrigger() {
        return taskTrigger;
    }

    public AITaskSystem getTaskSystem() {
        return system;
    }

    public AIHelper ai(){
        return getTaskSystem().getAIHelper();
    }

}
