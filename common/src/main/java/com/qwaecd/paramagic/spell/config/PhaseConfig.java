package com.qwaecd.paramagic.spell.config;

import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public class PhaseConfig {
    @Nonnull
    private final SpellPhaseType phaseType;
    /**
     * 阶段持续时间，0或负数表示无限
     */
    @Getter
    private final float duration;

    public PhaseConfig(@Nonnull SpellPhaseType phaseType, float duration) {
        this.phaseType = phaseType;
        this.duration = duration;
    }

    @Nonnull
    public SpellPhaseType getPhaseType() {
        return this.phaseType;
    }

    public static PhaseConfig create(@Nonnull SpellPhaseType phaseType, float duration) {
        return new PhaseConfig(phaseType, duration);
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
