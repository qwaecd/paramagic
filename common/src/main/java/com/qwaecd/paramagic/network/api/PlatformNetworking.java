package com.qwaecd.paramagic.network.api;

import net.minecraft.server.level.ServerPlayer;

public interface PlatformNetworking {
    void sendToPlayer(ServerPlayer target, Packet<?> packet);

    <T extends Packet<T>> void register(
            PacketIdentifier key,
            Class<T> packetClass,
            PacketFactory<T> factory,
            PacketHandler<T> handler
    );
}
