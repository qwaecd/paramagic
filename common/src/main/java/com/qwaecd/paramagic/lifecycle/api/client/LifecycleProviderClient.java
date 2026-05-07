package com.qwaecd.paramagic.lifecycle.api.client;

import com.qwaecd.paramagic.lifecycle.event.client.LocalPlayerLeaveWorldHandler;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

@PlatformScope(PlatformScopeType.CLIENT)
public interface LifecycleProviderClient {
    void registerOnPlayerLeaveWorld(LocalPlayerLeaveWorldHandler handler);
}
