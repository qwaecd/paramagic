package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.*;
import com.qwaecd.paramagic.network.codec.PacketByteBufCodec;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworking implements PlatformNetworking {
    @Override
    public void sendToPlayer(ServerPlayer target, Packet<?> packet) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        PacketByteBufCodec codec = new PacketByteBufCodec(buf);
        packet.encode(codec);
        ServerPlayNetworking.send(target, packet.getIdentifier().id, codec.getBuf());
    }

    @Override
    public <T extends Packet<T>> void register(
            PacketIdentifier key, Class<T> packetClass, PacketFactory<T> factory, PacketHandler<T> handler
    ) {
        if (key.direction == PacketDirection.CLIENT) {
            ClientNetworkRegister.register(key, packetClass, factory, handler);
        } else {
            ServerPlayNetworking.registerGlobalReceiver(key.id, (server, player, handler_, buf, responseSender) -> {
                T decode = factory.decode(new PacketByteBufCodec(buf));
                server.execute(() -> handler.handle(decode, new FabricNetworkContext(server, player, handler_, buf, responseSender)));
            });
        }
    }
}
