package com.qwaecd.paramagic.network.api;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("ClassCanBeRecord")
public class PacketIdentifier {
    @Getter
    public final ResourceLocation id;
    public final PacketDirection direction;

    public PacketIdentifier(ResourceLocation id, PacketDirection direction) {
        this.id = id;
        this.direction = direction;
    }

    public static PacketIdentifier handledInClient(ResourceLocation id) {
        return new PacketIdentifier(id, PacketDirection.CLIENT);
    }

    public static PacketIdentifier handledInServer(ResourceLocation id) {
        return new PacketIdentifier(id, PacketDirection.SERVER);
    }
}
