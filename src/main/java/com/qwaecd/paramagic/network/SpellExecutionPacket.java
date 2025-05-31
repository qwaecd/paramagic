package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.client.ClientSpellScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SpellExecutionPacket {
    private final String magicMapId;
    private final BlockPos center;
    private final Map<String, Object> parameters;

    public SpellExecutionPacket(String magicMapId, BlockPos center, Map<String, Object> parameters) {
        this.magicMapId = magicMapId;
        this.center = center;
        this.parameters = parameters;
    }

    public static void encode(SpellExecutionPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.magicMapId);
        buf.writeBlockPos(packet.center);
        buf.writeInt(packet.parameters.size());
        packet.parameters.forEach((key, value) -> {
            buf.writeUtf(key);
            buf.writeUtf(value.toString());
        });
    }

    public static SpellExecutionPacket decode(FriendlyByteBuf buf) {
        String magicMapId = buf.readUtf();
        BlockPos center = buf.readBlockPos();
        int paramCount = buf.readInt();
        Map<String, Object> parameters = new HashMap<>();
        for (int i = 0; i < paramCount; i++) {
            parameters.put(buf.readUtf(), buf.readUtf());
        }
        return new SpellExecutionPacket(magicMapId, center, parameters);
    }

    public static void handle(SpellExecutionPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientSpellScheduler.scheduleExecution(packet.magicMapId, packet.center, packet.parameters);
        });
        ctx.get().setPacketHandled(true);
    }
}
