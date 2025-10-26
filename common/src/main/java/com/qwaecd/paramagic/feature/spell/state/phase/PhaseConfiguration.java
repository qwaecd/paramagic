package com.qwaecd.paramagic.feature.spell.state.phase;

import com.qwaecd.paramagic.feature.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.feature.spell.state.transition.IPhaseTransition;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class PhaseConfiguration {
    private final SpellPhaseType phaseType;
    @Getter
    private final float duration; // 阶段持续时间，0或负数表示无限

    private final Map<String, IPhaseTransition<SpellStateMachine>> transitions = new HashMap<>();

    public PhaseConfiguration(SpellPhaseType phaseType, float duration) {
        this.phaseType = phaseType;
        this.duration = duration;
    }

    /**
     * 添加一个简单的、无条件的转换。<br>
     * {@code .addTransition("next", SpellPhaseType.COOLDOWN)}
     * @see com.qwaecd.paramagic.feature.spell.state.transition.AllTransEvents
     */
    public PhaseConfiguration addTransition(String event, SpellPhaseType nextPhase) {
        this.transitions.put(event, IPhaseTransition.to(nextPhase));
        return this;
    }

    /**
     * 添加一个基于Lambda的、有条件的转换。<br>
     * {@code .addTransition("next", (stateMachine) -> { ... return ...; })}
     * @see com.qwaecd.paramagic.feature.spell.state.transition.AllTransEvents
     */
    public PhaseConfiguration addTransition(String event, IPhaseTransition<SpellStateMachine> transition) {
        this.transitions.put(event, transition);
        return this;
    }

    /**
     * @see com.qwaecd.paramagic.feature.spell.state.transition.AllTransEvents
     */
    public IPhaseTransition<SpellStateMachine> getTransition(String event) {
        return this.transitions.get(event);
    }

    @SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
    public SpellPhaseType getPhaseType() {
        return this.phaseType;
    }
}
