package com.qwaecd.paramagic.lifecycle;

import com.qwaecd.paramagic.lifecycle.event.client.LocalPlayerLeaveWorldHandler;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@PlatformScope(PlatformScopeType.CLIENT)
public final class LifecycleBusClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleBusClient.class);
    LifecycleBusClient() {}

    private final List<LocalPlayerLeaveWorldHandler> localPlayerLeaveWorldHandlers = new ArrayList<>();

    public void registerLocalPlayerLeaveWorldHandler(LocalPlayerLeaveWorldHandler handler) {
        localPlayerLeaveWorldHandlers.add(handler);
    }

    public void fireLocalPlayerLeaveWorld() {
        for (LocalPlayerLeaveWorldHandler handler : localPlayerLeaveWorldHandlers) {
            try {
                handler.onLeave();
            } catch (Exception e) {
                LOGGER.error("Error while firing local player leave world event", e);
            }
        }
    }
}
