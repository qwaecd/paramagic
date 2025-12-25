package com.qwaecd.paramagic.network.packet.particle;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.network.particle.EffectSpawnData;
import com.qwaecd.paramagic.tools.ModRL;

public class S2CEffectSpawn implements Packet<S2CEffectSpawn> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInClient(ModRL.InModSpace("effect_spawn"));
    private final EffectSpawnData payload;

    public S2CEffectSpawn(EffectSpawnData payload) {
        this.payload = payload;
    }

    public EffectSpawnData getSpawnData() {
        return this.payload;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeObject("spawnData", this.payload);
    }

    public static S2CEffectSpawn decode(DataCodec codec) {
        EffectSpawnData spawnData = codec.readObject("spawnData", EffectSpawnData::fromCodec);
        return new S2CEffectSpawn(spawnData);
    }

    @Override
    public Class<S2CEffectSpawn> getPacketClass() {
        return S2CEffectSpawn.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
