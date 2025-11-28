package com.qwaecd.paramagic.mixinapi;

public interface IServerLevel {
    void registerOnLevelTick$paramagic(Runnable runnable);

    default void registerOnLevelTick(Runnable runnable) {
        this.registerOnLevelTick$paramagic(runnable);
    }
}
