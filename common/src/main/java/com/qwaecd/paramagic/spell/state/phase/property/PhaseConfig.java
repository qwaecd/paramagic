package com.qwaecd.paramagic.spell.state.phase.property;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;

import java.util.Objects;

public class PhaseConfig implements IDataSerializable {
    private final SpellPhaseType phaseType;
    @Getter
    private final float duration; // 阶段持续时间，0或负数表示无限

    public PhaseConfig(SpellPhaseType phaseType, float duration) {
        this.phaseType = phaseType;
        this.duration = duration;
    }

    @SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
    public SpellPhaseType getPhaseType() {
        return this.phaseType;
    }

    public static PhaseConfig create(SpellPhaseType phaseType, float duration) {
        return new PhaseConfig(phaseType, duration);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("type", this.phaseType.ID());
        codec.writeFloat("duration", this.duration);
    }

    public static PhaseConfig fromCodec(DataCodec codec) {
        int typeId = codec.readInt("type");
        SpellPhaseType phaseType = SpellPhaseType.fromID(typeId);
        float duration = codec.readFloat("duration");
        return PhaseConfig.create(phaseType, duration);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PhaseConfig that = (PhaseConfig) o;
        return Float.compare(duration, that.duration) == 0 && phaseType == that.phaseType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(phaseType, duration);
    }
}
