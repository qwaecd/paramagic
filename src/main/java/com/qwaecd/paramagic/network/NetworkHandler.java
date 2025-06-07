package com.qwaecd.paramagic.network;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import static com.qwaecd.paramagic.Paramagic.MODID;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void init() {
        INSTANCE.messageBuilder(MagicCirclePacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(MagicCirclePacket::encode)
                .decoder(MagicCirclePacket::decode)
                .consumerMainThread(MagicCirclePacket::handle)
                .add();
    }

    public static void sendToClientsInRange(ServerLevel level, Vec3 center, Object packet) {
        level.getChunkSource().chunkMap.getPlayers(level.getChunkAt(BlockPos.containing(center)).getPos(), false)
                .forEach(player -> INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
    }
}
