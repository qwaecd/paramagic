package com.qwaecd.paramagic.feature.effect;

import com.qwaecd.paramagic.core.particle.ParticleManager;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.feature.effect.exposion.EXPLOSION;

import java.util.HashMap;
import java.util.Map;

public class ClientEffectManager {
    private static ClientEffectManager INSTANCE;

    private final Map<String, EXPLOSION> activeExplosions = new HashMap<>();

    private ClientEffectManager() {
    }

    public static ClientEffectManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientEffectManager();
        }
        return INSTANCE;
    }

    public void addExplosion(String id, EXPLOSION explosion) {
        activeExplosions.put(id, explosion);
        MagicCircleManager.getInstance().addCircle(explosion.getMagicCircle());
        explosion.forEachEffect(e -> ParticleManager.getInstance().spawnEffect(e));
    }

    public void removeExplosion(String id) {
        EXPLOSION explosion = activeExplosions.remove(id);
        if (explosion == null) {
            return;
        }
        MagicCircleManager.getInstance().removeCircle(explosion.getMagicCircle());
        explosion.forEachEffect(e -> ParticleManager.getInstance().removeEffect(e));
    }

    public EXPLOSION getExplosion(String id) {
        return activeExplosions.get(id);
    }
}
