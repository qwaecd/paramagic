package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.core.particle.ParticleSystem;

public final class ParamagicDebugState {
    private static boolean showParticleInfo = false;

    private ParamagicDebugState() {
    }

    public static boolean showParticleInfo() {
        return showParticleInfo;
    }

    public static void setShowParticleInfo(boolean b) {
        showParticleInfo = b;
        if (ParticleSystem.isInitialized()) {
            ParticleSystem.getInstance().setDebugStatsEnabled(b);
        }
    }
}
