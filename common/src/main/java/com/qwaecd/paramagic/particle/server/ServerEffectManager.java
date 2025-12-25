package com.qwaecd.paramagic.particle.server;

import com.qwaecd.paramagic.particle.EffectSpawnBuilder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerEffectManager {
    private static ServerEffectManager INSTANCE;
    private final AtomicInteger atomicCounter;
    private final Map<Integer, ServerEffect> activeEffects = new HashMap<>();

    private ServerEffectManager() {
        this.atomicCounter = new AtomicInteger(1);
    }

    public static ServerEffectManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ServerEffectManager not initialized");
        }
        return INSTANCE;
    }

    public static void init() {
        if (INSTANCE != null) {
            return;
        }
        INSTANCE = new ServerEffectManager();
    }

    @Nullable
    public ServerEffect createEffect(EffectSpawnBuilder builder) {
        final int netId = this.atomicCounter.getAndIncrement();
        ServerEffect effect = new ServerEffect(netId, builder.build(netId));

        this.activeEffects.put(netId, effect);
        return effect;
    }

    @Nullable
    public ServerEffect getEffect(int netId) {
        return this.activeEffects.get(netId);
    }

    public void reset() {
        this.activeEffects.clear();
    }
}
