package com.qwaecd.paramagic.spell;

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

public class SpellConfiguration {
    @Nonnull
    @Getter
    private final SpellPhase initialPhase;

    private final Map<SpellPhaseType, SpellPhase> phaseInstanceMap = new EnumMap<>(SpellPhaseType.class);

    public SpellConfiguration(@Nonnull PhaseConfig initialPhaseCfg) {
        this.initialPhase = createPhaseFromConfig(initialPhaseCfg);
        this.addPhase(this.initialPhase);

        this.addPhaseConfig(initialPhaseCfg);
    }

    private void addPhase(SpellPhase phase) {
        this.phaseInstanceMap.put(phase.getPhaseType(), phase);
    }

    public SpellPhase getPhase(SpellPhaseType type) {
        return this.phaseInstanceMap.get(type);
    }


    public void addPhaseConfig(PhaseConfig phaseConfig) {
        SpellPhase phaseFromConfig = createPhaseFromConfig(phaseConfig);
        this.phaseInstanceMap.put(phaseConfig.getPhaseType(), phaseFromConfig);
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
}
