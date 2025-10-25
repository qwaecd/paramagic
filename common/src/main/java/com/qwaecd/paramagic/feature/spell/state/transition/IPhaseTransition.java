package com.qwaecd.paramagic.feature.spell.state.transition;

import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;

/**
 * 代表一个决策，它决定下一个阶段是什么。
 * @param <T> 决策时需要的上下文类型，例如 SpellStateMachine。
 */
@FunctionalInterface
public interface IPhaseTransition<T> {
    SpellPhaseType decideNextPhase(T context);

    static <T> IPhaseTransition<T> to(SpellPhaseType phaseType) {
        return context -> phaseType;
    }
}
