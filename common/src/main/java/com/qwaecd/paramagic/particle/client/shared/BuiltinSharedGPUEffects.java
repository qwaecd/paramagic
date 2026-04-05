package com.qwaecd.paramagic.particle.client.shared;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

@PlatformScope(PlatformScopeType.CLIENT)
public final class BuiltinSharedGPUEffects {
    public static final String MAGIC_ARROW_TRAIL = of("magic_arrow_trail");
    public static final String LASER_BEAM = of("laser_beam");
    public static final String CIRCLE_TRAIL = of("circle_trail");

    private static boolean initialized = false;

    private BuiltinSharedGPUEffects() {
    }

    private static String of(String name) {
        return Paramagic.MOD_ID + ":" + name;
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }

        SharedGPUEffectRegistry.registerTemplate(
                SharedGPUEffectTemplate.builder(MAGIC_ARROW_TRAIL)
                        .maxLifeTime(0.0f)
                        .build()
        );
        SharedGPUEffectRegistry.registerTemplate(
                SharedGPUEffectTemplate.builder(LASER_BEAM)
                        .maxLifeTime(0.0f)
                        .build()
        );
        SharedGPUEffectRegistry.registerTemplate(
                SharedGPUEffectTemplate.builder(CIRCLE_TRAIL)
                        .maxLifeTime(0.0f)
                        .physicsParameterSupplier(
                                () -> new PhysicsParamBuilder().build()
                        )
                        .build()
        );

        initialized = true;
    }
}
