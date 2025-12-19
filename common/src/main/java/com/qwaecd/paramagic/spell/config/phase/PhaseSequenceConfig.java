package com.qwaecd.paramagic.spell.config.phase;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
public class PhaseSequenceConfig implements IDataSerializable {
    @Nonnull
    private final SpellPhaseType initialPhaseType;
    private final Map<SpellPhaseType, PhaseConfig> phaseConfigMap;

    public PhaseSequenceConfig(@Nonnull PhaseConfig initialPhaseCfg) {
        this.initialPhaseType = initialPhaseCfg.getPhaseType();
        this.phaseConfigMap = new EnumMap<>(SpellPhaseType.class);
        this.phaseConfigMap.put(initialPhaseCfg.getPhaseType(), initialPhaseCfg);
    }

    private PhaseSequenceConfig(@Nonnull SpellPhaseType initialPhaseType, Map<SpellPhaseType, PhaseConfig> phaseConfigMap) {
        this.initialPhaseType = initialPhaseType;
        this.phaseConfigMap = phaseConfigMap;
    }

    public void addPhaseConfig(PhaseConfig phaseConfig) {
        this.phaseConfigMap.put(phaseConfig.getPhaseType(), phaseConfig);
    }

    public PhaseConfig getPhaseConfig(SpellPhaseType type) {
        return this.phaseConfigMap.get(type);
    }

    @Nonnull
    public PhaseConfig getInitialPhaseConfig() {
        return this.phaseConfigMap.get(this.initialPhaseType);
    }

    @Nonnull
    public SpellPhaseType getInitialPhaseType() {
        return this.initialPhaseType;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("initialType", this.initialPhaseType.ID());
        PhaseConfig[] phaseConfigs = this.phaseConfigMap.values().toArray(PhaseConfig[]::new);
        codec.writeObjectArray("phaseConfigs", phaseConfigs);
    }

    public static PhaseSequenceConfig fromCodec(DataCodec codec) {
        int initialTypeId = codec.readInt("initialType");
        SpellPhaseType initialType = SpellPhaseType.fromID(initialTypeId);
        IDataSerializable[] phaseConfigs = codec.readObjectArray("phaseConfigs", PhaseConfig::fromCodec);

        var phaseConfigMap = new EnumMap<SpellPhaseType, PhaseConfig>(SpellPhaseType.class);
        for (IDataSerializable obj : phaseConfigs) {
            PhaseConfig cfg = (PhaseConfig) obj;
            phaseConfigMap.put(cfg.getPhaseType(), cfg);
        }
        return new PhaseSequenceConfig(initialType, phaseConfigMap);
    }
}
