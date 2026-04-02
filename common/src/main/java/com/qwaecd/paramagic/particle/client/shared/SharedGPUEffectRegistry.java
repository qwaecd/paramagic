package com.qwaecd.paramagic.particle.client.shared;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@PlatformScope(PlatformScopeType.CLIENT)
public final class SharedGPUEffectRegistry {
    private static final long CREATE_RETRY_COOLDOWN_MS = 2000L;

    private static final Map<String, SharedGPUEffectTemplate> REGISTERED_TEMPLATES = new LinkedHashMap<>();
    private static final Map<String, GPUParticleEffect> ACTIVE_EFFECTS = new HashMap<>();
    private static final Map<String, Long> NEXT_CREATE_RETRY_AT = new HashMap<>();
    private static final Set<String> MISSING_TEMPLATE_WARNED = new HashSet<>();

    private SharedGPUEffectRegistry() {}

    public static void init() {
    }

    /**
     * Registers a shared GPU effect template.
     * <p>
     * Template keys are plain strings. Using the {@code modid:name} form is a
     * business convention and is intentionally not enforced here.
     * </p>
     * <p>
     * Shared templates must not rely on per-entity center-force behavior. If an
     * effect needs a moving force center per projectile, use a dedicated
     * {@link GPUParticleEffect} instead of this registry.
     * </p>
     */
    public static synchronized void registerTemplate(SharedGPUEffectTemplate template) {
        Objects.requireNonNull(template, "template");
        SharedGPUEffectTemplate previous = REGISTERED_TEMPLATES.put(template.getKey(), template);
        if (previous != null) {
            Paramagic.LOG.warn("Shared GPU effect template '{}' was replaced.", template.getKey());
        }
        MISSING_TEMPLATE_WARNED.remove(template.getKey());
    }

    @Nullable
    public static synchronized SharedGPUEffectTemplate getTemplate(String key) {
        return REGISTERED_TEMPLATES.get(key);
    }

    public static synchronized boolean containsTemplate(String key) {
        return REGISTERED_TEMPLATES.containsKey(key);
    }

    public static SharedGPUEffectRef ref(String key) {
        return new SharedGPUEffectRef(key);
    }

    @Nullable
    public static synchronized GPUParticleEffect getOrCreateSharedEffect(String key) {
        SharedGPUEffectTemplate template = REGISTERED_TEMPLATES.get(key);
        if (template == null) {
            warnMissingTemplate(key);
            return null;
        }

        GPUParticleEffect activeEffect = ACTIVE_EFFECTS.get(key);
        if (activeEffect != null) {
            return activeEffect;
        }

        ParticleSystem particleSystem = getParticleSystemIfReady();
        if (particleSystem == null) {
            return null;
        }

        long now = System.currentTimeMillis();
        Long nextRetryAt = NEXT_CREATE_RETRY_AT.get(key);
        if (nextRetryAt != null && now < nextRetryAt) {
            return null;
        }

        GPUParticleEffect createdEffect = template.createEffect();
        if (!particleSystem.spawnEffect(createdEffect)) {
            NEXT_CREATE_RETRY_AT.put(key, now + CREATE_RETRY_COOLDOWN_MS);
            Paramagic.LOG.warn("Failed to create shared GPU effect '{}' because no particle effect slot was available.", key);
            return null;
        }

        NEXT_CREATE_RETRY_AT.remove(key);
        ACTIVE_EFFECTS.put(key, createdEffect);
        return createdEffect;
    }

    public static boolean submit(String key, EmissionRequest request) {
        if (request == null || request.getCount() <= 0) {
            return false;
        }

        GPUParticleEffect effect = getOrCreateSharedEffect(key);
        if (effect == null) {
            return false;
        }

        effect.addExternalEmissionRequest(request);
        return true;
    }

    public static boolean submitFromEmitter(String key, Emitter emitter, float deltaTime) {
        Objects.requireNonNull(emitter, "emitter");
        emitter.update(deltaTime);
        EmissionRequest request = emitter.getEmissionRequest();
        return request != null && submit(key, request);
    }

    public static synchronized int getRegisteredTemplateCount() {
        return REGISTERED_TEMPLATES.size();
    }

    public static synchronized int getActiveEffectCount() {
        return ACTIVE_EFFECTS.size();
    }

    public static synchronized void reset() {
        ParticleSystem particleSystem = getParticleSystemIfReady();
        if (particleSystem != null) {
            for (GPUParticleEffect effect : ACTIVE_EFFECTS.values()) {
                particleSystem.removeEffect(effect);
            }
        }
        ACTIVE_EFFECTS.clear();
        NEXT_CREATE_RETRY_AT.clear();
    }

    @Nullable
    private static ParticleSystem getParticleSystemIfReady() {
        try {
            return ParticleSystem.getInstance();
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    private static void warnMissingTemplate(String key) {
        synchronized (SharedGPUEffectRegistry.class) {
            if (MISSING_TEMPLATE_WARNED.add(key)) {
                Paramagic.LOG.warn("Shared GPU effect template '{}' was requested before being registered.", key);
            }
        }
    }
}
