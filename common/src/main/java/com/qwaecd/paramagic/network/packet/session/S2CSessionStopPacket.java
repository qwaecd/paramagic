package com.qwaecd.paramagic.network.packet.session;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.tools.ModRL;

import java.util.UUID;

public class S2CSessionStopPacket implements Packet<S2CSessionStopPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInClient(ModRL.inModSpace("session_stop"));

    private final UUID sessionId;
    private final EndSpellReason reason;

    public S2CSessionStopPacket(UUID sessionId, EndSpellReason reason) {
        this.sessionId = sessionId;
        this.reason = reason;
    }

    public UUID getSessionId() {
        return this.sessionId;
    }

    public EndSpellReason getReason() {
        return this.reason;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeUUID("sessionId", this.sessionId);
        codec.writeString("reason", this.reason.getName());
    }

    public static S2CSessionStopPacket decode(DataCodec codec) {
        UUID sessionId = codec.readUUID("sessionId");
        EndSpellReason reason = EndSpellReason.fromName(codec.readString("reason"));
        return new S2CSessionStopPacket(sessionId, reason);
    }

    @Override
    public Class<S2CSessionStopPacket> getPacketClass() {
        return S2CSessionStopPacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
