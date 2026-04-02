package com.qwaecd.paramagic.particle.client.shared;

import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Objects;

@Getter
@PlatformScope(PlatformScopeType.CLIENT)
public final class SharedGPUEffectRef {
    private final String key;

    SharedGPUEffectRef(String key) {
        this.key = Objects.requireNonNull(key, "key");
    }

    @Nullable
    public GPUParticleEffect getOrCreateEffect() {
        return SharedGPUEffectRegistry.getOrCreateSharedEffect(this.key);
    }

    public boolean submit(EmissionRequest request) {
        return SharedGPUEffectRegistry.submit(this.key, request);
    }

    public boolean submitFromEmitter(Emitter emitter, float deltaTime) {
        return SharedGPUEffectRegistry.submitFromEmitter(this.key, emitter, deltaTime);
    }
}
