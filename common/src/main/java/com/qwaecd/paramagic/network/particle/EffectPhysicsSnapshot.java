package com.qwaecd.paramagic.network.particle;

import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;

public class EffectPhysicsSnapshot implements IDataSerializable {
    @Getter
    public final int netId;
    @Getter
    public final int seq;
    private final EffectPhysicsParameter source;

    public EffectPhysicsSnapshot(int netId, int seq, EffectPhysicsParameter source) {
        this.netId = netId;
        this.seq = seq;
        this.source = source;
    }

    public void applyTo(EffectPhysicsParameter target) {
        target.applyFrom(this.source);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("netId", netId);
        codec.writeInt("seq", seq);
        codec.writeObject("source", source);
    }

    public static EffectPhysicsSnapshot fromCodec(DataCodec codec) {
        int netId = codec.readInt("netId");
        int seq = codec.readInt("seq");
        EffectPhysicsParameter source = codec.readObject("source", EffectPhysicsParameter::fromCodec);
        return new EffectPhysicsSnapshot(netId, seq, source);
    }
}
