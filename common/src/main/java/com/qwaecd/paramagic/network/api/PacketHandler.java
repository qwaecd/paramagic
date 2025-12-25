package com.qwaecd.paramagic.network.api;

public interface PacketHandler<T extends Packet<T>> {
    void handle(T packet, NetworkContext context);
}
