package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.*;
import com.qwaecd.paramagic.network.codec.PacketByteBufCodec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworkRegister {
    public static <T extends Packet<T>> void register(
            PacketIdentifier key, Class<T> packetClass, PacketFactory<T> factory, PacketHandler<T> handler
    ) {
        ClientPlayNetworking.registerGlobalReceiver(key.id, (client, handler_, buf, responseSender) -> {
            T decode = factory.decode(new PacketByteBufCodec(buf));
            client.execute(() -> handler.handle(decode, new NetworkContext()));
        });
    }
}
