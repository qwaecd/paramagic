package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketFactory;
import com.qwaecd.paramagic.network.api.PacketHandler;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.network.api.PlatformNetworking;
import com.qwaecd.paramagic.network.codec.PacketByteBufCodec;
import com.qwaecd.paramagic.tools.ModRL;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;

/** Forge SimpleChannel adapter used by the common networking facade. */
public final class ForgeNetworking implements PlatformNetworking {
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ModRL.inModSpace("network"),
            () -> Networking.PROTOCOL_VERSION,
            Networking.PROTOCOL_VERSION::equals,
            Networking.PROTOCOL_VERSION::equals
    );

    private final Map<ResourceLocation, PacketHandler<?>> clientHandlers = new HashMap<>();
    private int nextMessageId;

    @Override
    public void sendToPlayer(ServerPlayer target, Packet<?> packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> target), packet);
    }

    @Override
    public void sendToServer(Packet<?> packet) {
        CHANNEL.sendToServer(packet);
    }

    @Override
    public <T extends Packet<T>> void register(
            PacketIdentifier key, Class<T> packetClass, PacketFactory<T> factory, PacketHandler<T> handler
    ) {
        CHANNEL.messageBuilder(packetClass, nextMessageId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder((packet, buffer) -> packet.encode(new PacketByteBufCodec(buffer)))
                .decoder(buffer -> factory.decode(new PacketByteBufCodec(buffer)))
                .consumerMainThread((packet, contextSupplier) -> {
                    var context = contextSupplier.get();
                    handler.handle(packet, new NetworkContext(context.getSender()));
                    context.setPacketHandled(true);
                })
                .add();
    }

    @Override
    public <T extends Packet<T>> void registerClientbound(
            PacketIdentifier key, Class<T> packetClass, PacketFactory<T> factory
    ) {
        CHANNEL.messageBuilder(packetClass, nextMessageId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder((packet, buffer) -> packet.encode(new PacketByteBufCodec(buffer)))
                .decoder(buffer -> factory.decode(new PacketByteBufCodec(buffer)))
                .consumerMainThread((packet, contextSupplier) -> {
                    dispatchClientbound(key.id, packet);
                    contextSupplier.get().setPacketHandled(true);
                })
                .add();
    }

    @Override
    public <T extends Packet<T>> void registerClientHandler(PacketIdentifier key, PacketHandler<T> handler) {
        clientHandlers.put(key.id, handler);
    }

    @SuppressWarnings("unchecked")
    private <T extends Packet<T>> void dispatchClientbound(ResourceLocation id, T packet) {
        PacketHandler<T> handler = (PacketHandler<T>) clientHandlers.get(id);
        if (handler != null) {
            handler.handle(packet, new NetworkContext());
        }
    }
}
