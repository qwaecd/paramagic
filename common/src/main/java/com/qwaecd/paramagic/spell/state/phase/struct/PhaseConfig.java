package com.qwaecd.paramagic.spell.state.phase.struct;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.spell.struct.phase.PhaseAssetConfig;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public class PhaseConfig implements IDataSerializable {
    private final SpellPhaseType phaseType;
    @Getter
    private final float duration; // 阶段持续时间，0或负数表示无限

    @Nullable
    @Getter
    private final PhaseAssetConfig assetConfig;

    public PhaseConfig(SpellPhaseType phaseType, float duration, @Nullable PhaseAssetConfig assetConfig) {
        this.phaseType = phaseType;
        this.duration = duration;
        this.assetConfig = assetConfig;
    }

    public PhaseConfig(SpellPhaseType phaseType, float duration) {
        this(phaseType, duration, null);
    }

    @SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
    public SpellPhaseType getPhaseType() {
        return this.phaseType;
    }

    public static PhaseConfig create(SpellPhaseType phaseType, float duration) {
        return new PhaseConfig(phaseType, duration);
    }

    public static PhaseConfig create(SpellPhaseType phaseType, float duration, PhaseAssetConfig assetConfig) {
        return new PhaseConfig(phaseType, duration, assetConfig);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("type", this.phaseType.ID());
        codec.writeFloat("duration", this.duration);
        codec.writeObjectNullable("assetConfig", this.assetConfig);
    }

    public static PhaseConfig fromCodec(DataCodec codec) {
        int typeId = codec.readInt("type");
        SpellPhaseType phaseType = SpellPhaseType.fromID(typeId);
        float duration = codec.readFloat("duration");
        PhaseAssetConfig assetConfig = codec.readObjectNullable("assetConfig", PhaseAssetConfig::fromCodec);
        return PhaseConfig.create(phaseType, duration, assetConfig);
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
