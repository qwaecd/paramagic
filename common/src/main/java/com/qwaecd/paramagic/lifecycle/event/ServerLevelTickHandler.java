package com.qwaecd.paramagic.lifecycle.event;

import net.minecraft.server.level.ServerLevel;

public interface ServerLevelTickHandler {
    void tick(ServerLevel level);
}
