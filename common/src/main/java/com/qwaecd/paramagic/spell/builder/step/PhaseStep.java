package com.qwaecd.paramagic.spell.builder.step;

import com.qwaecd.paramagic.spell.core.SpellDefinition;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;


public interface PhaseStep {
    PhaseStep phase(SpellPhaseType type, float duration);
    AssetStart phaseWithAssets(SpellPhaseType type, float duration);

    SpellDefinition build();
}
