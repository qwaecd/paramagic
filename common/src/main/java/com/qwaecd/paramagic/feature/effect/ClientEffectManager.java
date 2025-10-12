package com.qwaecd.paramagic.feature.effect;

import com.qwaecd.paramagic.core.particle.ParticleManager;
import com.qwaecd.paramagic.feature.MagicCircleManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientEffectManager {
    private static ClientEffectManager INSTANCE;

    private final Map<UUID, EXPLOSION> activeExplosions = new HashMap<>();

    private ClientEffectManager() {
    }

    public static ClientEffectManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientEffectManager();
        }
        return INSTANCE;
    }

    public void addExplosion(UUID playerId, EXPLOSION explosion) {
        activeExplosions.put(playerId, explosion);
        MagicCircleManager.getInstance().addCircle(explosion.getMagicCircle());
        explosion.forEachEffect(e -> ParticleManager.getInstance().spawnEffect(e));
    }

    public void removeExplosion(UUID playerId) {
        EXPLOSION explosion = activeExplosions.remove(playerId);
        if (explosion == null) {
            return;
        }
        MagicCircleManager.getInstance().removeCircle(explosion.getMagicCircle());
        explosion.forEachEffect(e -> ParticleManager.getInstance().removeEffect(e));
    }

    public EXPLOSION getExplosion(UUID playerId) {
        return activeExplosions.get(playerId);
    }
}
