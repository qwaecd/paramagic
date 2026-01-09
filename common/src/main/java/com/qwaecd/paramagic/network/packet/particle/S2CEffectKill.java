package com.qwaecd.paramagic.network.packet.particle;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.network.particle.EffectKillData;
import com.qwaecd.paramagic.tools.ModRL;

public class S2CEffectKill implements Packet<S2CEffectKill> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInClient(ModRL.InModSpace("effect_kill"));
    private final EffectKillData payload;

    public S2CEffectKill(EffectKillData payload) {
        this.payload = payload;
    }

    public EffectKillData getKillData() {
        return this.payload;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeObject("killData", this.payload);
    }

    public static S2CEffectKill decode(DataCodec codec) {
        EffectKillData killData = codec.readObject("killData", EffectKillData::fromCodec);
        return new S2CEffectKill(killData);
    }

    @Override
    public Class<S2CEffectKill> getPacketClass() {
        return S2CEffectKill.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
