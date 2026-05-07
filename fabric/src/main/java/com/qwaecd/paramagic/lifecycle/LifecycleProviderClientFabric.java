package com.qwaecd.paramagic.lifecycle;

import com.qwaecd.paramagic.lifecycle.api.client.LifecycleProviderClient;
import com.qwaecd.paramagic.lifecycle.event.client.LocalPlayerLeaveWorldHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public final class LifecycleProviderClientFabric implements LifecycleProviderClient {
    @Override
    public void registerOnPlayerLeaveWorld(LocalPlayerLeaveWorldHandler handler) {
        ClientPlayConnectionEvents.DISCONNECT.register((handler1, client) -> handler.onLeave());
    }
}
