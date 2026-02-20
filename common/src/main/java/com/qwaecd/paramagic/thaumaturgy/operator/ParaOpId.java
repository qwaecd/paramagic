package com.qwaecd.paramagic.thaumaturgy.operator;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.tools.ModRL;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ParaOpId implements IDataSerializable {
    @Getter
    @Nonnull
    public final ResourceLocation id;

    @Nonnull
    public final OperatorType type;

    public final float cycleCooldown;
    public final float transmissionDelay;

    private ParaOpId(@Nonnull ResourceLocation id, @Nonnull Properties prop) {
        this.id = id;
        this.type = prop.type;
        this.cycleCooldown = prop.cycleCooldown;
        this.transmissionDelay = prop.transmissionDelay;
    }

    public static ParaOpId of(@Nonnull ResourceLocation id, Properties prop) {
        return new ParaOpId(id, prop);
    }

    public static ParaOpId from(String namespace, String path, Properties prop) {
        return ParaOpId.of(ModRL.of(namespace, path), prop);
    }

    public float getCycleCooldown() {
        return this.cycleCooldown;
    }

    public float getTransmissionDelay() {
        return this.transmissionDelay;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParaOpId other = (ParaOpId) o;
        return Objects.equals(this.id, other.id);
    }

    public static final String OPERATOR_ID_KEY = "operatorId";

    @Override
    public void write(DataCodec codec) {
        codec.writeString(OPERATOR_ID_KEY, this.id.toString());
    }

    public static ParaOpId fromCodec(DataCodec codec) {
        String idStr = codec.readString(OPERATOR_ID_KEY);
        ParaOpId paraOpId = AllParaOperators.getIdByString(idStr);
        if (paraOpId == null) {
            throw new IllegalArgumentException("Unknown ParaOpId: " + idStr);
        }
        return paraOpId;
    }

    public static class Properties {
        public final OperatorType type;
        public float cycleCooldown;
        public float transmissionDelay;

        public Properties(OperatorType type, float transmissionDelay, float cycleCooldown) {
            this.type = type;
            this.transmissionDelay = transmissionDelay;
            this.cycleCooldown = cycleCooldown;
        }

        public Properties(OperatorType type) {
            this(type, 0.01f, 0.05f);
        }

        public Properties cycleCooldown(float cycleCooldown) {
            this.cycleCooldown = cycleCooldown;
            return this;
        }

        public Properties transmissionDelay(float transmissionDelay) {
            this.transmissionDelay = transmissionDelay;
            return this;
        }
    }
}
