package org.astemir.uniblend.core.particle.beta;


import org.astemir.uniblend.core.UniblendRegistry;

public class UniblendBetaParticlesHandler extends UniblendRegistry.Concurrent<BetaParticleEmitter>{

    public static UniblendBetaParticlesHandler INSTANCE;
    public UniblendBetaParticlesHandler() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate(long tick) {
        INSTANCE.getEntries().forEach((entry)->entry.update((int)tick));
    }
}
