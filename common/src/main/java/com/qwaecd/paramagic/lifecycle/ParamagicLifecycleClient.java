package com.qwaecd.paramagic.lifecycle;

import com.qwaecd.paramagic.client.CameraShake;
import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.lifecycle.api.client.LifecycleProviderClient;
import com.qwaecd.paramagic.particle.client.ClientEffectRepository;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRegistry;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.core.SessionManagers;

@PlatformScope(PlatformScopeType.CLIENT)
public final class ParamagicLifecycleClient {
    private static boolean initialized;

    private static LifecycleBusClient bus;

    private ParamagicLifecycleClient() {}

    public static synchronized void init(LifecycleProviderClient provider) {
        if (initialized) {
            throw new IllegalStateException("ParamagicLifecycleClient has already been initialized");
        }
        bus = new LifecycleBusClient();
        registerAll(bus);
        provider.registerOnPlayerLeaveWorld(bus::fireLocalPlayerLeaveWorld);
        initialized = true;
    }

    private static void registerAll(LifecycleBusClient bus) {
        bus.registerLocalPlayerLeaveWorldHandler(() -> SessionManagers.getForClient().reset());
        bus.registerLocalPlayerLeaveWorldHandler(() -> ClientEffectRepository.getInstance().reset());
        bus.registerLocalPlayerLeaveWorldHandler(SharedGPUEffectRegistry::reset);
        bus.registerLocalPlayerLeaveWorldHandler(() -> MagicCircleManager.getInstance().reset());
        bus.registerLocalPlayerLeaveWorldHandler(() -> {
            if (ModRenderSystem.isInitialized()) {
                ModRenderSystem.getInstance().clearAll();
            }
        });
        bus.registerLocalPlayerLeaveWorldHandler(() -> {
            if (ParticleSystem.isInitialized()) {
                ParticleSystem.getInstance().reset();
            }
        });
        bus.registerLocalPlayerLeaveWorldHandler(CameraShake::reset);
    }
}
