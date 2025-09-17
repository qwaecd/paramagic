package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.Paramagic;

public class ParticleManager {
    private static ParticleManager INSTANCE;

    private ParticleManager() {}

    public static ParticleManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ParticleManager has not been initialized.");
        }
        return INSTANCE;
    }

    public static void init() {
        if (INSTANCE != null) {
            Paramagic.LOG.warn("ParticleManager is already initialized.");
            return;
        }
        INSTANCE = new ParticleManager();
        ParticleMeshes.init();
    }
}
