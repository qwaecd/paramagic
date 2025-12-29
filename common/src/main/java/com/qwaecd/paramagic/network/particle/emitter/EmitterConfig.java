package com.qwaecd.paramagic.network.particle.emitter;

import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Arrays;

@SuppressWarnings("ClassCanBeRecord")
public final class EmitterConfig implements IDataSerializable {
    public final int emitterType;
    public final float particlesPerSecond;
    public final Vector3f emitterPosition;
    @Nullable
    public final EmitterPropertyConfig propertyConfig;

    @Nullable
    public final ParticleBurst[] bursts;

    public EmitterConfig(
            int emitterType,
            float particlesPerSecond,
            Vector3f emitterPosition,
            @Nullable EmitterPropertyConfig propertyConfig,
            @Nullable ParticleBurst[] bursts
    ) {
        this.emitterType = emitterType;
        this.particlesPerSecond = particlesPerSecond;
        this.emitterPosition = emitterPosition;
        this.propertyConfig = propertyConfig;
        this.bursts = bursts;
    }

    public EmitterConfig(
            EmitterType emitterType,
            float particlesPerSecond,
            Vector3f emitterPosition,
            EmitterPropertyConfig propertyConfig,
            ParticleBurst[] bursts
    ) {
        this(emitterType.id, particlesPerSecond, emitterPosition, propertyConfig, bursts);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("emitterType", emitterType);
        codec.writeFloat("particlesPerSecond", particlesPerSecond);
        codec.writeVector3f("emitterPosition", emitterPosition);
        codec.writeObjectNullable("propertyConfig", propertyConfig);
        if (this.bursts == null) {
            codec.writeBoolean("hasBursts", false);
        } else {
            codec.writeBoolean("hasBursts", true);
            BurstWrapper[] wrapperArr = Arrays.stream(this.bursts).map(BurstWrapper::new).toArray(BurstWrapper[]::new);
            codec.writeObjectArray("bursts", wrapperArr);
        }
    }

    public static EmitterConfig fromCodec(DataCodec codec) {
        int emitterId = codec.readInt("emitterType");
        EmitterType emitterType = EmitterType.fromId(emitterId);

        float particlesPerSecond = codec.readFloat("particlesPerSecond");
        Vector3f emitterPosition = codec.readVector3f("emitterPosition");
        EmitterPropertyConfig propertyConfig = codec.readObjectNullable("propertyConfig", EmitterPropertyConfig::fromCodec);
        ParticleBurst[] bursts;
        boolean hasBursts = codec.readBoolean("hasBursts");
        if (hasBursts) {
            IDataSerializable[] dataArray = codec.readObjectArray("bursts", BurstWrapper::fromCodec);
            BurstWrapper[] wrapperArr = DataCodec.castObjectArray(dataArray, BurstWrapper[]::new);
            bursts = Arrays.stream(wrapperArr).map(BurstWrapper::toParticleBurst).toArray(ParticleBurst[]::new);
        } else {
            bursts = null;
        }
        return new EmitterConfig(emitterType, particlesPerSecond, emitterPosition, propertyConfig, bursts);
    }

    private static class BurstWrapper implements IDataSerializable {
        final ParticleBurst burst;

        BurstWrapper(ParticleBurst burst) {
            this.burst = burst;
        }

        @Override
        public void write(DataCodec codec) {
            codec.writeFloat("timeInEmitterLife", this.burst.getTimeInEmitterLife());
            codec.writeInt("count", this.burst.getCount());
        }

        public static BurstWrapper fromCodec(DataCodec codec) {
            float timeInEmitterLife = codec.readFloat("timeInEmitterLife");
            int count = codec.readInt("count");
            return new BurstWrapper(new ParticleBurst(timeInEmitterLife, count));
        }

        public ParticleBurst toParticleBurst() {
            return this.burst;
        }
    }
}
