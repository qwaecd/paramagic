package com.qwaecd.paramagic.network.packet.session;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.spell.session.store.SessionDataSyncPayload;
import com.qwaecd.paramagic.tools.ModRL;

public class S2CSessionDataSyncPacket implements Packet<S2CSessionDataSyncPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInClient(ModRL.InModSpace("session_data_sync"));
    private final SessionDataSyncPayload payload;

    public S2CSessionDataSyncPacket(SessionDataSyncPayload payload) {
        this.payload = payload;
    }

    public SessionDataSyncPayload getSyncData() {
        return this.payload;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeObject("syncData", this.payload);
    }

    public static S2CSessionDataSyncPacket decode(DataCodec codec) {
        SessionDataSyncPayload payload = codec.readObject("syncData", SessionDataSyncPayload::fromCodec);
        return new S2CSessionDataSyncPacket(payload);
    }

    @Override
    public Class<S2CSessionDataSyncPacket> getPacketClass() {
        return S2CSessionDataSyncPacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
