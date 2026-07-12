package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.*;
import com.qwaecd.paramagic.network.codec.PacketByteBufCodec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class FabricNetworking implements PlatformNetworking {
    private final Map<ResourceLocation, ClientboundPacket<?>> clientboundPackets = new HashMap<>();
    @Override
    public void sendToPlayer(ServerPlayer target, Packet<?> packet) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        PacketByteBufCodec codec = new PacketByteBufCodec(buf);
        packet.encode(codec);
        ServerPlayNetworking.send(target, packet.getIdentifier().id, codec.getBuf());
    }

    @Override
    public void sendToServer(Packet<?> packet) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        PacketByteBufCodec codec = new PacketByteBufCodec(buf);
        packet.encode(codec);
        ClientPlayNetworking.send(packet.getIdentifier().id, codec.getBuf());
    }

    @Override
    public <T extends Packet<T>> void register(
            PacketIdentifier key, Class<T> packetClass, PacketFactory<T> factory, PacketHandler<T> handler
    ) {
        if (key.direction != PacketDirection.SERVER) {
            throw new IllegalArgumentException("Serverbound packet registration expected: " + key.id);
        }
        ServerPlayNetworking.registerGlobalReceiver(key.id, (server, player, handler_, buf, responseSender) -> {
            T decode = factory.decode(new PacketByteBufCodec(buf));
            server.execute(() -> handler.handle(decode, new FabricNetworkContext(server, player, handler_, buf, responseSender)));
        });
    }

    @Override
    public <T extends Packet<T>> void registerClientbound(PacketIdentifier key, Class<T> packetClass, PacketFactory<T> factory) {
        if (key.direction != PacketDirection.CLIENT) {
            throw new IllegalArgumentException("Clientbound packet registration expected: " + key.id);
        }
        clientboundPackets.put(key.id, new ClientboundPacket<>(key, packetClass, factory));
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T extends Packet<T>> void registerClientHandler(PacketIdentifier key, PacketHandler<T> handler) {
        ClientboundPacket packet = clientboundPackets.get(key.id);
        if (packet == null) {
            throw new IllegalArgumentException("Clientbound packet was not registered: " + key.id);
        }
        ClientNetworkRegister.register(packet.key(), packet.packetClass(), packet.factory(), handler);
    }

    private record ClientboundPacket<T extends Packet<T>>(
            PacketIdentifier key, Class<T> packetClass, PacketFactory<T> factory
    ) {
    }
}
