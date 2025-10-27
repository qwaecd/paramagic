package com.qwaecd.paramagic.feature.spell;

import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.PhaseConfiguration;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;
import com.qwaecd.paramagic.feature.spell.state.phase.struct.impl.CastingPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.struct.impl.IdlePhase;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SpellConfiguration {
    @Nonnull
    @Getter
    private final SpellPhase initialPhase;

    private final Map<SpellPhaseType, PhaseConfiguration> phaseConfigMap = new HashMap<>();

    private final Map<SpellPhaseType, SpellPhase> phaseInstanceMap = new EnumMap<>(SpellPhaseType.class);

    public SpellConfiguration(@Nonnull PhaseConfiguration initialPhaseCfg) {
        this.initialPhase = createPhaseFromConfig(initialPhaseCfg);
        this.addPhase(this.initialPhase);

        this.addPhaseConfig(initialPhaseCfg);
    }

    public void addPhase(SpellPhase phase) {
        this.phaseInstanceMap.put(phase.getPhaseType(), phase);
    }

    public SpellPhase getPhase(SpellPhaseType type) {
        return this.phaseInstanceMap.get(type);
    }


    public void addPhaseConfig(PhaseConfiguration phaseConfig) {
        this.phaseConfigMap.put(phaseConfig.getPhaseType(), phaseConfig);
    }

    public static SpellPhase createPhaseFromConfig(PhaseConfiguration cfg) {
        SpellPhaseType phaseType = cfg.getPhaseType();
        switch (phaseType) {
            case IDLE -> {
                return new IdlePhase(cfg);
            }
            case CASTING -> {
                return new CastingPhase(cfg);
            }
            // TODO: 实现其他的阶段类型
            default -> throw new IllegalArgumentException("Unknown phase type: " + phaseType);
        }
    }
}
