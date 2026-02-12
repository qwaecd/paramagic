package com.qwaecd.paramagic.spell.phase;

import com.qwaecd.paramagic.spell.config.PhaseConfig;
import com.qwaecd.paramagic.spell.phase.impl.CastingPhase;
import com.qwaecd.paramagic.spell.phase.impl.ChannelingPhase;
import com.qwaecd.paramagic.spell.phase.impl.CooldownPhase;
import com.qwaecd.paramagic.spell.phase.impl.IdlePhase;

public class PhaseFactory {
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
