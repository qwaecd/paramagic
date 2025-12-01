package com.qwaecd.paramagic.spell.struct;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;
import com.qwaecd.paramagic.spell.state.phase.struct.impl.CastingPhase;
import com.qwaecd.paramagic.spell.state.phase.struct.impl.ChannelingPhase;
import com.qwaecd.paramagic.spell.state.phase.struct.impl.CooldownPhase;
import com.qwaecd.paramagic.spell.state.phase.struct.impl.IdlePhase;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

public class SpellConfig implements IDataSerializable {
    @Nonnull
    @Getter
    private final SpellPhase initialPhase;

    private final Map<SpellPhaseType, SpellPhase> phaseInstanceMap = new EnumMap<>(SpellPhaseType.class);

    public SpellConfig(@Nonnull PhaseConfig initialPhaseCfg) {
        this.initialPhase = createPhaseFromConfig(initialPhaseCfg);
        this.addPhase(this.initialPhase);
    }

    private void addPhase(SpellPhase phase) {
        this.phaseInstanceMap.put(phase.getPhaseType(), phase);
    }

    public SpellPhase getPhase(SpellPhaseType type) {
        return this.phaseInstanceMap.get(type);
    }


    public void addPhaseConfig(PhaseConfig phaseConfig) {
        SpellPhase phaseFromConfig = createPhaseFromConfig(phaseConfig);
        this.addPhase(phaseFromConfig);
    }

    public static SpellPhase createPhaseFromConfig(PhaseConfig cfg) {
        SpellPhaseType phaseType = cfg.getPhaseType();
        switch (phaseType) {
            case IDLE -> {
                return new IdlePhase(cfg);
            }
            case CASTING -> {
                return new CastingPhase(cfg);
            }
            case CHANNELING -> {
                return new ChannelingPhase(cfg);
            }
            case COOLDOWN -> {
                return new CooldownPhase(cfg);
            }
            default -> throw new IllegalArgumentException("Unknown phase type: " + phaseType);
        }
    }

    @Override
    public void write(DataCodec codec) {
        final int count = this.phaseInstanceMap.size();
        codec.writeInt("size", count);

        Object[] phases = this.phaseInstanceMap.values().toArray();
        codec.writeObject("phase_0", this.initialPhase.getConfig());
        int i = 1;
        for (Object phase : phases) {
            // Skip the initial phase by comparing the phase type to avoid instance inequality issues
            if (((SpellPhase) phase).getPhaseType() == this.initialPhase.getPhaseType()) {
                continue;
            }
            PhaseConfig cfg = ((SpellPhase) phase).getConfig();
            codec.writeObject("phase_" + i, cfg);
            i++;
        }
    }

    public static SpellConfig fromCodec(DataCodec codec) {
        final int count = codec.readInt("size");

        PhaseConfig[] phaseConfigs = new PhaseConfig[count];
        for (int i = 0; i < count; i++) {
            phaseConfigs[i] = codec.readObject("phase_" + i, PhaseConfig::fromCodec);
        }
        SpellConfig spellConfig = new SpellConfig(phaseConfigs[0]);
        for (int i = 1; i < count; i++) {
            spellConfig.addPhaseConfig(phaseConfigs[i]);
        }
        return spellConfig;
    }
}
