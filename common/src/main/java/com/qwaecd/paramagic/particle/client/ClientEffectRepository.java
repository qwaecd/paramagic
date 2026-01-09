package com.qwaecd.paramagic.particle.client;

import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.network.particle.EffectSpawnData;
import com.qwaecd.paramagic.network.particle.emitter.EmitterConfig;
import com.qwaecd.paramagic.network.particle.emitter.EmitterFactory;
import com.qwaecd.paramagic.particle.api.EmitterFactoryRegistry;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

import javax.annotation.Nullable;
import java.util.*;

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

    public void spawnEffect(EffectSpawnData data) {
        List<Emitter> list = Arrays.stream(data.emitterConfig)
                .map(ClientEffectRepository::createEmitter)
                .filter(Objects::nonNull)
                .toList();
        EffectPhysicsParameter parameter = new EffectPhysicsParameter();
        data.physicsSnapshot.applyTo(parameter);
        GPUParticleEffect effect = new GPUParticleEffect(
                list,
                data.maxParticles,
                data.maxLifeTime,
                parameter
        );

        // 生成效果可能因为达到效果上限或其他原因而失败
        boolean success = ParticleSystem.getInstance().spawnEffect(effect);
        if (success) {
            this.activeEffects.put(data.netId, new ClientEffect(data.netId, effect));
        }
    }

    public void removeEffect(int netId) {
        ClientEffect effect = this.activeEffects.remove(netId);
        if (effect != null) {
            effect.close();
        }
    }

    @Nullable
    private static Emitter createEmitter(EmitterConfig config) {
        EmitterFactory factory = EmitterFactoryRegistry.getFactory(config.emitterType);
        if (factory != null) {
            return factory.createEmitter(config);
        }
        return null;
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
