package com.qwaecd.paramagic.lifecycle;

import com.qwaecd.paramagic.lifecycle.api.LifecycleProvider;
import com.qwaecd.paramagic.particle.server.ServerEffectManager;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.core.SessionManagers;
import com.qwaecd.paramagic.spell.mana.ManaRegeneration;
import com.qwaecd.paramagic.world.explosion.CustomExplosionManager;

@PlatformScope(PlatformScopeType.SERVER)
public final class ParamagicLifecycle {
    private static boolean initialized;

    private static LifecycleBus bus;

    private ParamagicLifecycle() {}

    public static synchronized void init(LifecycleProvider provider) {
        if (initialized) {
            throw new IllegalStateException("ParamagicLifecycle has already been initialized");
        }
        bus = new LifecycleBus();
        registerAll(bus);
        provider.registerServerTick(bus::fireServerTick);
        provider.registerServerLevelTick(bus::fireServerLevelTick);
        provider.registerServerStopping(bus::fireServerStopping);
        initialized = true;
    }

    private static void registerAll(LifecycleBus bus) {
        ServerEffectManager.init();
        bus.registerServerTickHandler(() -> ServerEffectManager.getInstance().tick(1.0f / 20.0f));

        bus.registerServerLevelTickHandler(CustomExplosionManager::tick);
        bus.registerServerLevelTickHandler(SessionManagers::tickServerLevel);
        bus.registerServerLevelTickHandler(ManaRegeneration::tick);

        bus.registerServerStoppingHandler(SessionManagers::clearServerSessions);
        bus.registerServerStoppingHandler(() -> ServerEffectManager.getInstance().reset());
        bus.registerServerStoppingHandler(CustomExplosionManager::reset);
    }
}
