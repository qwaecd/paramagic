package com.qwaecd.paramagic.particle.client.shared;

import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Getter
@PlatformScope(PlatformScopeType.CLIENT)
public final class SharedGPUEffectTemplate {
    private final String key;
    private final int maxParticleCount;
    private final float maxLifeTime;
    private final Supplier<List<Emitter>> emitterSupplier;
    private final Supplier<EffectPhysicsParameter> physicsParameterSupplier;

    private SharedGPUEffectTemplate(Builder builder) {
        this.key = Objects.requireNonNull(builder.key, "key");
        this.maxParticleCount = builder.maxParticleCount;
        this.maxLifeTime = builder.maxLifeTime;
        this.emitterSupplier = builder.emitterSupplier;
        this.physicsParameterSupplier = builder.physicsParameterSupplier;
    }

    public static Builder builder(String key) {
        return new Builder(key);
    }

    public GPUParticleEffect createEffect() {
        List<Emitter> emitters = this.emitterSupplier.get();
        EffectPhysicsParameter physicsParameter = this.physicsParameterSupplier.get();
        return new GPUParticleEffect(
                emitters == null ? new ArrayList<>() : new ArrayList<>(emitters),
                this.maxParticleCount,
                this.maxLifeTime,
                physicsParameter == null ? new EffectPhysicsParameter() : physicsParameter
        );
    }

    private static EffectPhysicsParameter copyOf(EffectPhysicsParameter source) {
        EffectPhysicsParameter copy = new EffectPhysicsParameter();
        copy.applyFrom(source);
        return copy;
    }

    public static final class Builder {
        private final String key;
        private int maxParticleCount = 32768;
        private float maxLifeTime = 0.0f;
        private Supplier<List<Emitter>> emitterSupplier = List::of;
        private Supplier<EffectPhysicsParameter> physicsParameterSupplier = EffectPhysicsParameter::new;

        private Builder(String key) {
            this.key = key;
        }

        public Builder maxParticleCount(int maxParticleCount) {
            if (maxParticleCount <= 0) {
                throw new IllegalArgumentException("maxParticleCount must be greater than 0");
            }
            this.maxParticleCount = maxParticleCount;
            return this;
        }

        public Builder maxLifeTime(float maxLifeTime) {
            this.maxLifeTime = maxLifeTime;
            return this;
        }

        public Builder emitterSupplier(Supplier<List<Emitter>> emitterSupplier) {
            this.emitterSupplier = Objects.requireNonNull(emitterSupplier, "emitterSupplier");
            return this;
        }

        public Builder emitters(List<Emitter> emitters) {
            List<Emitter> emitterList = emitters == null ? List.of() : List.copyOf(emitters);
            this.emitterSupplier = () -> emitterList;
            return this;
        }

        public Builder physicsParameterSupplier(Supplier<EffectPhysicsParameter> physicsParameterSupplier) {
            this.physicsParameterSupplier = Objects.requireNonNull(physicsParameterSupplier, "physicsParameterSupplier");
            return this;
        }

        public Builder physicsParameter(EffectPhysicsParameter physicsParameter) {
            Objects.requireNonNull(physicsParameter, "physicsParameter");
            this.physicsParameterSupplier = () -> copyOf(physicsParameter);
            return this;
        }

        public SharedGPUEffectTemplate build() {
            return new SharedGPUEffectTemplate(this);
        }
    }
}
