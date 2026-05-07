package com.qwaecd.paramagic.lifecycle;

import com.qwaecd.paramagic.lifecycle.event.ServerLevelTickHandler;
import com.qwaecd.paramagic.lifecycle.event.ServerTickHandler;
import com.qwaecd.paramagic.lifecycle.event.ServerStoppingHandler;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@PlatformScope(PlatformScopeType.SERVER)
public final class LifecycleBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleBus.class);
    LifecycleBus() {}

    private final List<ServerTickHandler> serverTickHandlers = new ArrayList<>();
    private final List<ServerLevelTickHandler> serverLevelTickHandlers = new ArrayList<>();
    private final List<ServerStoppingHandler> serverStoppingHandlers = new ArrayList<>();

    public void registerServerTickHandler(ServerTickHandler handler) {
        serverTickHandlers.add(handler);
    }

    public void registerServerLevelTickHandler(ServerLevelTickHandler handler) {
        serverLevelTickHandlers.add(handler);
    }

    public void registerServerStoppingHandler(ServerStoppingHandler handler) {
        serverStoppingHandlers.add(handler);
    }

    public void fireServerTick() {
        for (ServerTickHandler handler : serverTickHandlers) {
            try {
                handler.tick();
            } catch (Exception e) {
                LOGGER.error("Error while firing server tick event", e);
            }
        }
    }

    public void fireServerLevelTick(ServerLevel level) {
        for (ServerLevelTickHandler handler : serverLevelTickHandlers) {
            try {
                handler.tick(level);
            } catch (Exception e) {
                LOGGER.error("Error while firing server level: '{}'", level, e);
            }
        }
    }

    public void fireServerStopping() {
        for (ServerStoppingHandler handler : serverStoppingHandlers) {
            try {
                handler.onStop();
            } catch (Exception e) {
                LOGGER.error("Error while firing server stopping event", e);
            }
        }
    }
}
