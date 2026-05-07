package com.qwaecd.paramagic.lifecycle.api;

import com.qwaecd.paramagic.lifecycle.event.ServerLevelTickHandler;
import com.qwaecd.paramagic.lifecycle.event.ServerTickHandler;
import com.qwaecd.paramagic.lifecycle.event.ServerStoppingHandler;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

@PlatformScope(PlatformScopeType.SERVER)
public interface LifecycleProvider {
    void registerServerTick(ServerTickHandler handler);

    void registerServerLevelTick(ServerLevelTickHandler handler);

    void registerServerStopping(ServerStoppingHandler handler);
}
