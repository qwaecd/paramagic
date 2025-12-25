package com.qwaecd.paramagic.particle.client;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@PlatformScope(PlatformScopeType.CLIENT)
public class ClientEffectRepository {
    private static ClientEffectRepository INSTANCE;
    private final Map<Integer, ClientEffect> activeEffects = new HashMap<>();

    private ClientEffectRepository() {

    }

    public static ClientEffectRepository getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ClientEffectRepository not initialized");
        }
        return INSTANCE;
    }

    public static void init() {
        if (INSTANCE != null) {
            return;
        }
        INSTANCE = new ClientEffectRepository();
    }

    @Nullable
    public ClientEffect getEffect(int netId) {
        return this.activeEffects.get(netId);
    }

    public void reset() {
        this.activeEffects.forEach((netId, clientEffect) -> clientEffect.close());
        this.activeEffects.clear();
    }
}
