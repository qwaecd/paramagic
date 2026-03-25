package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

import javax.annotation.Nonnull;

public class C2SSubmitEditedParaDataPacket implements Packet<C2SSubmitEditedParaDataPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(ModRL.inModSpace("submit_edited_para_data"));

    @Nonnull
    private final ParaData paraData;
    private final long cacheToken;
    private final int cacheVersion;

    public C2SSubmitEditedParaDataPacket(@Nonnull ParaData paraData, long cacheToken, int cacheVersion) {
        this.paraData = paraData;
        this.cacheToken = cacheToken;
        this.cacheVersion = cacheVersion;
    }

    @Nonnull
    public ParaData getParaData() {
        return this.paraData;
    }

    public long getCacheToken() {
        return this.cacheToken;
    }

    public int getCacheVersion() {
        return this.cacheVersion;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeObject("paraData", this.paraData);
        codec.writeLong("cacheToken", this.cacheToken);
        codec.writeInt("cacheVersion", this.cacheVersion);
    }

    public static C2SSubmitEditedParaDataPacket decode(DataCodec codec) {
        ParaData paraData = codec.readObject("paraData", ParaData::fromCodec);
        long cacheToken = codec.readLong("cacheToken");
        int cacheVersion = codec.readInt("cacheVersion");
        return new C2SSubmitEditedParaDataPacket(paraData, cacheToken, cacheVersion);
    }

    @Override
    public Class<C2SSubmitEditedParaDataPacket> getPacketClass() {
        return C2SSubmitEditedParaDataPacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
