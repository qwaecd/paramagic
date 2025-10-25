package com.qwaecd.paramagic.feature.spell;

import com.qwaecd.paramagic.feature.spell.state.phase.ISpellPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.PhaseConfiguration;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class SpellConfiguration {
    @Nonnull
    @Getter
    private final ISpellPhase initialPhase;

    private final Map<SpellPhaseType, PhaseConfiguration> phaseConfigMap = new HashMap<>();

    public SpellConfiguration(@Nonnull ISpellPhase initialPhase) {
        this.initialPhase = initialPhase;
    }

    public void addPhaseConfig(PhaseConfiguration phaseConfig) {
        this.phaseConfigMap.put(phaseConfig.getPhaseType(), phaseConfig);
    }

    public PhaseConfiguration getPhaseConfig(SpellPhaseType phaseType) {
        return this.phaseConfigMap.get(phaseType);
    }
}
