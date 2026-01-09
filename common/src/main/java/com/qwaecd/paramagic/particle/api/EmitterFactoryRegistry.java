package com.qwaecd.paramagic.particle.api;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.exception.EmitterPropertyTypeException;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.core.particle.emitter.impl.*;
import com.qwaecd.paramagic.network.particle.emitter.EmitterConfig;
import com.qwaecd.paramagic.network.particle.emitter.EmitterFactory;
import com.qwaecd.paramagic.network.particle.emitter.EmitterPropertyConfig;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 用于将已经注册的 {@link EmitterFactory} 与 {@link EmitterType} 或 int 关联起来的注册表。<br>
 * emitterType.id -> EmitterFactory<br>
 * 在服务端上不存在该注册表，因为发射器的创建仅在客户端进行。
 */
@PlatformScope(PlatformScopeType.CLIENT)
public class EmitterFactoryRegistry {
    private static final Map<Integer, EmitterFactory> EMITTER_FACTORIES = new ConcurrentHashMap<>();

    /**
     * EmitterType -> "how to build the concrete emitter".<br>
     * Kept separate from {@link #EMITTER_FACTORIES} so registerAll() stays tiny and extendable.
     */
    private static final EnumMap<EmitterType, Function<EmitterConfig, EmitterBase>> DEFAULT_EMITTER_BUILDERS = new EnumMap<>(EmitterType.class);

    static {
        DEFAULT_EMITTER_BUILDERS.put(EmitterType.CIRCLE, cfg -> new CircleEmitter(cfg.emitterPosition, cfg.particlesPerSecond));
        DEFAULT_EMITTER_BUILDERS.put(EmitterType.CUBE,   cfg -> new CubeEmitter(cfg.emitterPosition, cfg.particlesPerSecond));
        DEFAULT_EMITTER_BUILDERS.put(EmitterType.LINE,   cfg -> new LineEmitter(cfg.emitterPosition, cfg.particlesPerSecond));
        DEFAULT_EMITTER_BUILDERS.put(EmitterType.POINT,  cfg -> new PointEmitter(cfg.emitterPosition, cfg.particlesPerSecond));
        DEFAULT_EMITTER_BUILDERS.put(EmitterType.SPHERE, cfg -> new SphereEmitter(cfg.emitterPosition, cfg.particlesPerSecond));
    }

    public static void register(EmitterType emitterType, EmitterFactory factory) {
        register(emitterType.id, factory);
    }

    public static void register(int emitterId, EmitterFactory factory) {
        if (EMITTER_FACTORIES.containsKey(emitterId)) {
            throw new IllegalStateException("EmitterFactory for EmitterType id " + emitterId + " is already registered.");
        }
        EMITTER_FACTORIES.put(emitterId, factory);
    }

    @Nullable
    public static EmitterFactory getFactory(int emitterType) {
        return EMITTER_FACTORIES.get(emitterType);
    }

    public static void registerAll() {
        for (EmitterType type : EmitterType.values()) {
            Function<EmitterConfig, EmitterBase> builder = DEFAULT_EMITTER_BUILDERS.get(type);
            if (builder == null) {
                throw new IllegalStateException("No default emitter builder registered for type: " + type);
            }
            register(type, config -> createWithConfig(config, builder));
        }
    }

    private static Emitter createWithConfig(EmitterConfig config, Function<EmitterConfig, EmitterBase> builder) {
        EmitterBase emitter = builder.apply(config);
        if (config.bursts != null) {
            for (ParticleBurst burst : config.bursts) {
                emitter.addBurst(burst);
            }
        }

        EmitterPropertyConfig propertyConfig = config.propertyConfig;
        if (propertyConfig != null) {
            for (var propertyValue : propertyConfig.properties) {
                try {
                    emitter.setPropertyUnsafe(propertyValue.propertyKey, propertyValue.value);
                } catch (EmitterPropertyTypeException e) {
                    Paramagic.LOG.warn("Failed to set property '{}' on emitter type {}: {}", propertyValue.propertyKey.getName(), EmitterType.fromId(config.emitterType), e.getMessage());
                }
            }
        }
        return emitter;
    }
}
