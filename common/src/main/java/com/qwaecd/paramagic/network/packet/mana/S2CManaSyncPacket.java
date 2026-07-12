package com.qwaecd.paramagic.network.packet.mana;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

public final class S2CManaSyncPacket implements Packet<S2CManaSyncPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInClient(
            ModRL.inModSpace("mana_sync")
    );

    private final int mana;
    private final int maxMana;

    public S2CManaSyncPacket(int mana, int maxMana) {
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeInt("mana", mana);
        codec.writeInt("maxMana", maxMana);
    }

    public static S2CManaSyncPacket decode(DataCodec codec) {
        return new S2CManaSyncPacket(codec.readInt("mana"), codec.readInt("maxMana"));
    }

    @Override
    public Class<S2CManaSyncPacket> getPacketClass() {
        return S2CManaSyncPacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
