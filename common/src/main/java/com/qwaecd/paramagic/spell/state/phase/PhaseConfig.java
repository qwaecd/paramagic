package com.qwaecd.paramagic.spell.state.phase;

import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.state.internal.event.transition.AllTransEvents;
import com.qwaecd.paramagic.spell.state.internal.event.transition.IPhaseTransition;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class PhaseConfig {
    private final SpellPhaseType phaseType;
    @Getter
    private final float duration; // 阶段持续时间，0或负数表示无限

    private final Map<String, IPhaseTransition<SpellStateMachine>> transitions = new HashMap<>();

    public PhaseConfig(SpellPhaseType phaseType, float duration) {
        this.phaseType = phaseType;
        this.duration = duration;
    }

    /**
     * 添加一个简单的、无条件的转换。<br>
     * {@code .addTransition("next", SpellPhaseType.COOLDOWN)}
     * @see AllTransEvents
     */
    public PhaseConfig addTransition(String event, SpellPhaseType nextPhase) {
        this.transitions.put(event, IPhaseTransition.to(nextPhase));
        return this;
    }

    @SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
    public SpellPhaseType getPhaseType() {
        return this.phaseType;
    }
}
