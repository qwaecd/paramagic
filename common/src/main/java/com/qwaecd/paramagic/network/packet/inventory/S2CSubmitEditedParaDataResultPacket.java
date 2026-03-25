package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

public class S2CSubmitEditedParaDataResultPacket implements Packet<S2CSubmitEditedParaDataResultPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInClient(ModRL.inModSpace("submit_edited_para_data_result"));

    private final boolean success;
    private final long cacheToken;
    private final int cacheVersion;

    public S2CSubmitEditedParaDataResultPacket(boolean success, long cacheToken, int cacheVersion) {
        this.success = success;
        this.cacheToken = cacheToken;
        this.cacheVersion = cacheVersion;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public long getCacheToken() {
        return this.cacheToken;
    }

    public int getCacheVersion() {
        return this.cacheVersion;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeBoolean("success", this.success);
        codec.writeLong("cacheToken", this.cacheToken);
        codec.writeInt("cacheVersion", this.cacheVersion);
    }

    public static S2CSubmitEditedParaDataResultPacket decode(DataCodec codec) {
        boolean success = codec.readBoolean("success");
        long cacheToken = codec.readLong("cacheToken");
        int cacheVersion = codec.readInt("cacheVersion");
        return new S2CSubmitEditedParaDataResultPacket(success, cacheToken, cacheVersion);
    }

    @Override
    public Class<S2CSubmitEditedParaDataResultPacket> getPacketClass() {
        return S2CSubmitEditedParaDataResultPacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
