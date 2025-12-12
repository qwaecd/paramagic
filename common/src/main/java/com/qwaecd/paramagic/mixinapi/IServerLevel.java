package com.qwaecd.paramagic.mixinapi;

import net.minecraft.server.level.ServerLevel;

import java.util.function.Consumer;

public interface IServerLevel {
    void registerOnLevelTick$paramagic(Consumer<ServerLevel> callback);

    default void registerOnLevelTick(Consumer<ServerLevel> callback) {
        this.registerOnLevelTick$paramagic(callback);
    }
}
