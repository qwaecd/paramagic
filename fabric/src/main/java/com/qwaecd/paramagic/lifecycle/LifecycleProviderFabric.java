package com.qwaecd.paramagic.lifecycle;

import com.qwaecd.paramagic.lifecycle.api.LifecycleProvider;
import com.qwaecd.paramagic.lifecycle.event.ServerLevelTickHandler;
import com.qwaecd.paramagic.lifecycle.event.ServerStoppingHandler;
import com.qwaecd.paramagic.lifecycle.event.ServerTickHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public final class LifecycleProviderFabric implements LifecycleProvider {
    @Override
    public void registerServerTick(ServerTickHandler handler) {
        ServerTickEvents.END_SERVER_TICK.register(server -> handler.tick());
    }

    @Override
    public void registerServerLevelTick(ServerLevelTickHandler handler) {
        ServerTickEvents.END_WORLD_TICK.register(handler::tick);
    }

    @Override
    public void registerServerStopping(ServerStoppingHandler handler) {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> handler.onStop());
    }
}
