package org.astemir.uniblend.core.entity.ai;

import org.astemir.uniblend.core.entity.ai.tasks.AITask;
import org.astemir.uniblend.core.entity.ai.triggers.TaskTrigger;
import org.astemir.uniblend.core.entity.parent.UEntity;
import org.astemir.uniblend.core.entity.parent.UMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class AITaskSystem {

    private List<AITask> tasks = new ArrayList<>();
    private CopyOnWriteArrayList<Entity> sensedEntities = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<AITask> runningTasks = new CopyOnWriteArrayList<>();
    private List<EntityType> senseForEntities = new ArrayList<>();
    private Predicate<Entity> sensePredicate = (entity)->true;
    private AIHelper aiHelper = new AIHelper(this);
    private float updateSensedEntitiesTime = 20;
    private UMob entity;

    public void senseForEntities(EntityType... types){
        senseForEntities = Arrays.asList(types);
    }

    public void senseForEntities(Predicate<Entity> predicate,EntityType... types){
        senseForEntities = Arrays.asList(types);
        sensePredicate = predicate;
    }

    public AITaskSystem(UMob entity) {
        this.entity = entity;
    }

    public AITaskSystem registerTask(AITask task){
        task.register(this);
        tasks.add(task);
        tasks.sort(Comparator.comparingInt(AITask::getPriority));
        return this;
    }

    public boolean runTask(AITask task){
        if (canRunTask(task)) {
            for (int i : task.getCanInterrupt()) {
                AITask interruptedTask = getRunningTaskById(i);
                if (interruptedTask != null){
                    interruptedTask.stop();
                }
            }
            task.start();
            runningTasks.add(task);
            runningTasks.sort(Comparator.comparingInt(AITask::getPriority));
            return true;
        }
        return false;
    }


    public boolean runTask(int id){
        AITask task = getTaskById(id);
        return runTask(task);
    }


    public void stopTask(int id){
        AITask task = getRunningTaskById(id);
        if (task != null) {
            stopTask(task);
        }
    }

    public void stopTask(AITask task){
        task.stop();
        runningTasks.remove(task);
        runningTasks.sort(Comparator.comparingInt(AITask::getPriority));
    }

    public void forceRunTask(AITask task){
        if (!task.isRunning()){
            for (AITask runningTask : runningTasks) {
                if (runningTask.getLayer() == task.getLayer()){
                    stopTask(runningTask);
                }
            }
            task.start();
            runningTasks.add(task);
            runningTasks.sort(Comparator.comparingInt(AITask::getPriority));
        }
    }

    public void forceRunTask(int id){
        forceRunTask(getTaskById(id));
    }

    public void update(){
        if (entity.getTicks() % updateSensedEntitiesTime == 0) {
            float followRange = entity.getViewDistance();
            sensedEntities = new CopyOnWriteArrayList<>(entity.getHandle().getWorld().getNearbyEntities(entity.getHandle().getLocation(), followRange, followRange, followRange, (testEntity) -> senseForEntities.contains(testEntity.getType()) && sensePredicate.test(testEntity)));
        }
        for (AITask runningTask : runningTasks) {
            if (runningTask.isRunning()) {
                if (runningTask.canContinue() && !runningTask.isConflictingWithOtherTask()) {
                    runningTask.update();
                } else {
                    stopTask(runningTask);
                }
            }else{
                stopTask(runningTask);
            }
        }
        for (AITask task : tasks) {
            if (task.getTaskTrigger() == TaskTrigger.AUTO_ENABLE) {
                runTask(task);
            }
        }
    }

    public void handlePlayerInteraction(Player player, EquipmentSlot hand, ItemStack itemStack){
        for (AITask task : tasks) {
            if (task.getTaskTrigger() == TaskTrigger.INTERACTION) {
                if (canRunTask(task)) {
                    if (task.onInteract(player, hand, itemStack)) {
                        runTask(task);
                    }
                }
            }
        }
    }

    public void handleHurt(EntityDamageEvent.DamageCause source, float damage){
        for (AITask task : tasks) {
            if (task.getTaskTrigger() == TaskTrigger.HURT) {
                if (canRunTask(task)) {
                    if (task.onHurt(source, damage)) {
                        runTask(task);
                    }
                }
            }
        }
    }

    public boolean isRunning(int id){
        return getRunningTaskById(id) != null;
    }

    public boolean canRunTask(AITask task){
        return task.canStart() && !task.isConflictingWithOtherTask() && !task.isRunning();
    }

    public <K extends AITask> K getRunningTaskById(int id){
        for (AITask runningTask : runningTasks) {
            if (runningTask.getId() == id){
                return (K)runningTask;
            }
        }
        return null;
    }

    public <K extends AITask> K getRunningTask(Class<K> className){
        for (AITask runningTask : runningTasks) {
            if (runningTask.getClass() == className){
                return (K)runningTask;
            }
        }
        return null;
    }

    public <K extends AITask> K getTaskById(int id){
        for (AITask task : tasks) {
            if (task.getId() == id){
                return (K)task;
            }
        }
        return null;
    }

    public AITaskSystem senseUpdateTime(float updateSensedEntitiesTime) {
        this.updateSensedEntitiesTime = updateSensedEntitiesTime;
        return this;
    }

    public CopyOnWriteArrayList<Entity> getSensedEntities() {
        return sensedEntities;
    }

    public <T extends Entity> T getNearbyEntity(Class<T> entityClass, Predicate<T> predicate){
        T nearbyEntity = null;
        for (Entity sensedEntity : sensedEntities) {
            if (sensedEntity.getClass() == entityClass){
                if (predicate.test((T) sensedEntity)) {
                    if (nearbyEntity == null) {
                        nearbyEntity = (T) sensedEntity;
                    } else {
                        if (sensedEntity.getLocation().distanceSquared(entity.getHandle().getLocation()) < nearbyEntity.getLocation().distanceSquared(entity.getHandle().getLocation())) {
                            nearbyEntity = (T) sensedEntity;
                        }
                    }
                }
            }
        }
        return nearbyEntity;
    }


    public List<AITask> getTasks() {
        return tasks;
    }

    public CopyOnWriteArrayList<AITask> getRunningTasks() {
        return runningTasks;
    }

    public AIHelper getAIHelper() {
        return aiHelper;
    }

    public UMob getEntity() {
        return entity;
    }
}
