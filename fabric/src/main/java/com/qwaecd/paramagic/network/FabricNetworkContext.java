package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.NetworkContext;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class FabricNetworkContext extends NetworkContext {

    public FabricNetworkContext(
            MinecraftServer server,
            ServerPlayer player,
            ServerGamePacketListenerImpl handler,
            FriendlyByteBuf buf,
            PacketSender responseSender
    ) {
        super(player);
    }
}
