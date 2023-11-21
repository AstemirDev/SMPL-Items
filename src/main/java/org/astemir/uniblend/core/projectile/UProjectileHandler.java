package org.astemir.uniblend.core.projectile;

import org.astemir.uniblend.core.UniblendRegistry;

public class UProjectileHandler extends UniblendRegistry.Concurrent<UProjectile>{

    public static UProjectileHandler INSTANCE;
    public UProjectileHandler() {
        INSTANCE = this;
    }
    @Override
    public void onUpdate(long tick) {
        for (UProjectile entry : INSTANCE.getEntries()) {
            if (!entry.isRemoved()) {
                entry.update();
            }else{
                remove(entry);
            }
        }
    }
}
