package com.qwaecd.paramagic.particle.client.shared;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

@PlatformScope(PlatformScopeType.CLIENT)
public final class BuiltinSharedGPUEffects {
    public static final String MAGIC_ARROW_TRAIL = Paramagic.MOD_ID + ":magic_arrow_trail";

    private static boolean initialized = false;

    private BuiltinSharedGPUEffects() {
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

        initialized = true;
    }
}
