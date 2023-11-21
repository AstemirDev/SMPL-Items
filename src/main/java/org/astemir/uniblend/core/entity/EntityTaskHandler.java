package org.astemir.uniblend.core.entity;

import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.core.UniblendRegistry;
import org.bukkit.entity.Entity;

public class EntityTaskHandler extends UniblendRegistry.Concurrent<EntityTaskHandler.EntityRunnable> {

    public static EntityTaskHandler INSTANCE;
    public EntityTaskHandler() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate(long tick) {
        for (EntityRunnable runnable : getEntries()) {
            if (runnable.getEntity() == null){
                runnable.runOut(runnable.getEntity(),tick);
                remove(runnable);
            }else
            if (runnable.isCancelled() || runnable.getEntity().isDead()){
                runnable.runOut(runnable.getEntity(),tick);
                remove(runnable);
            }else {
                if (runnable.getEndLife() == -1) {
                    runnable.run(runnable.getEntity(), tick);
                }else{
                    if (tick < runnable.getEndLife()){
                        runnable.run(runnable.getEntity(), tick);
                    }else{
                        runnable.runOut(runnable.getEntity(),tick);
                        runnable.getEntity().remove();
                        remove(runnable);
                    }
                }
            }
        }
    }



    public abstract static class EntityRunnable{

        private Entity entity;
        private boolean cancelled = false;
        private long endLife = -1;

        public EntityRunnable(Entity entity) {
            this.entity = entity;
        }

        public void runOut(Entity entity, long ticks){}

        public Entity getEntity() {
            return entity;
        }

        public void cancel(){
            this.cancelled = true;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public abstract void run(Entity entity, long ticks);


        public long getEndLife() {
            return endLife;
        }

        public EntityRunnable lifespan(int life){
            endLife = UniblendCorePlugin.GLOBAL_TICKS+life;
            return this;
        }
    }
}
