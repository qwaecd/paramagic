package com.qwaecd.paramagic.network.api;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class NetworkContext {
    @Nullable
    private final ServerPlayer player;

    public NetworkContext() {
        this.player = null;
    }

    public NetworkContext(@Nullable ServerPlayer player) {
        this.player = player;
    }

    @Nullable
    public ServerPlayer getPlayer() {
        return this.player;
    }
}
