package com.qwaecd.paramagic.network.packet.session;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

import java.util.UUID;

public class C2SSessionAttachPacket implements Packet<C2SSessionAttachPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(ModRL.inModSpace("session_attach"));

    private final UUID sessionId;

    public C2SSessionAttachPacket(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID getSessionId() {
        return this.sessionId;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeUUID("sessionId", this.sessionId);
    }

    public static C2SSessionAttachPacket decode(DataCodec codec) {
        return new C2SSessionAttachPacket(codec.readUUID("sessionId"));
    }

    @Override
    public Class<C2SSessionAttachPacket> getPacketClass() {
        return C2SSessionAttachPacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
