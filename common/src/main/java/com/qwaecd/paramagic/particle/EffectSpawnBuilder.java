package com.qwaecd.paramagic.particle;

import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.network.particle.EffectPhysicsSnapshot;
import com.qwaecd.paramagic.network.particle.EffectSpawnData;
import com.qwaecd.paramagic.network.particle.anchor.AnchorSpec;
import com.qwaecd.paramagic.network.particle.emitter.EmitterConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("UnusedReturnValue")
public class EffectSpawnBuilder {
    private static final Random random = new Random();
    private int maxParticles = 1;
    private float maxLifetime = 5.0f;
    private final List<EmitterConfig> emitterConfigs = new ArrayList<>();
    private long seed = random.nextLong();
    private EffectPhysicsParameter parameter = new EffectPhysicsParameter();
    private AnchorSpec anchorSpec = AnchorSpec.STATIC_ORIGIN;

    public EffectSpawnBuilder() {}

    public EffectSpawnBuilder setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
        return this;
    }

    public EffectSpawnBuilder setMaxLifetime(float maxLifetime) {
        this.maxLifetime = maxLifetime;
        return this;
    }

    public EffectSpawnBuilder addEmitterConfig(EmitterConfig config) {
        this.emitterConfigs.add(config);
        return this;
    }

    public EffectSpawnBuilder setSeed(long seed) {
        this.seed = seed;
        return this;
    }

    public EffectSpawnBuilder setEffectPhysicsParameter(EffectPhysicsParameter parameter) {
        this.parameter = parameter;
        return this;
    }

    public EffectSpawnBuilder setAnchorSpec(AnchorSpec anchorSpec) {
        this.anchorSpec = anchorSpec;
        return this;
    }

    public EffectSpawnData build(int netId) {
        EmitterConfig[] configsArray = this.emitterConfigs.toArray(new EmitterConfig[0]);
        EffectPhysicsSnapshot snapshot = new EffectPhysicsSnapshot(netId, 0, this.parameter);
        return new EffectSpawnData(
                netId,
                this.maxParticles,
                this.maxLifetime,
                this.seed,
                configsArray,
                snapshot,
                this.anchorSpec
        );
    }
}
