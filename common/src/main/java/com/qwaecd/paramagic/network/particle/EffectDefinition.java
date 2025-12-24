package com.qwaecd.paramagic.network.particle;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.particle.anchor.AnchorSpec;
import com.qwaecd.paramagic.network.particle.emitter.EmitterConfig;
import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
public class EffectDefinition implements IDataSerializable {
    @Getter
    public final int netId;
    @Getter
    public final EmitterConfig[] emitterConfig;
    // unused for now
    @Getter
    public final long seed;
    @Getter
    public final EffectPhysicsSnapshot physicsSnapshot;
    @Getter
    public final AnchorSpec anchorSpec;

    public EffectDefinition(
            int netId,
            long seed,
            EmitterConfig[] emitterConfig,
            EffectPhysicsSnapshot physicsSnapshot,
            AnchorSpec anchorSpec
    ) {
        this.netId = netId;
        this.seed = seed;
        this.emitterConfig = emitterConfig;
        this.physicsSnapshot = physicsSnapshot;
        this.anchorSpec = anchorSpec;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("netId", this.netId);
        codec.writeObjectArray("emitterConfig", this.emitterConfig);
        codec.writeLong("seed", this.seed);
        codec.writeObject("physicsSnapshot", this.physicsSnapshot);
        codec.writeObject("anchorSpec", this.anchorSpec);
    }

    public static EffectDefinition fromCodec(DataCodec codec) {
        int netId = codec.readInt("netId");
        IDataSerializable[] dataArray = codec.readObjectArray("emitterConfig", EmitterConfig::fromCodec);
        EmitterConfig[] emitterConfig = DataCodec.castObjectArray(dataArray, EmitterConfig[]::new);
        long seed = codec.readLong("seed");
        var physicsSnapshot = codec.readObject("physicsSnapshot", EffectPhysicsSnapshot::fromCodec);
        AnchorSpec anchorSpec = codec.readObject("anchorSpec", AnchorSpec::fromCodec);
        return new EffectDefinition(netId, seed, emitterConfig, physicsSnapshot, anchorSpec);
    }
}
