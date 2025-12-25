package com.qwaecd.paramagic.network.particle;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.core.particle.emitter.impl.*;
import com.qwaecd.paramagic.network.particle.emitter.EmitterConfig;
import com.qwaecd.paramagic.network.particle.emitter.EmitterFactory;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class EffectTypeRegistry {
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
            register(type, config -> createWithBursts(config, builder));
        }
    }

    private static Emitter createWithBursts(EmitterConfig config, Function<EmitterConfig, EmitterBase> builder) {
        EmitterBase emitter = builder.apply(config);
        if (config.bursts != null) {
            for (ParticleBurst burst : config.bursts) {
                emitter.addBurst(burst);
            }
        }
        return emitter;
    }
}
