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

    public static void init(Ticker ticker) {
        if (INSTANCE != null) {
            return;
        }
        INSTANCE = new ServerEffectManager();
        ticker.register(() -> INSTANCE.tick(1.0f / 20.0f));
    }

    public interface Ticker {
        void register(Runnable tickFunction);
    }

    public void tick(float deltaTime) {
        this.activeEffects.values().removeIf(effect -> {
            effect.tick(deltaTime);
            return effect.isExpired();
        });
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
