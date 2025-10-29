package com.qwaecd.paramagic.spell.state.phase.property;

import lombok.Getter;

public class PhaseConfig {
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
}
