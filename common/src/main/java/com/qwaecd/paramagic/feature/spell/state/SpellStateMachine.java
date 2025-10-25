package com.qwaecd.paramagic.feature.spell.state;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.feature.spell.SpellConfiguration;
import com.qwaecd.paramagic.feature.spell.state.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.feature.spell.state.phase.ISpellPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.PhaseConfiguration;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;
import com.qwaecd.paramagic.feature.spell.state.phase.impl.CastingPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.impl.IdlePhase;
import com.qwaecd.paramagic.feature.spell.state.transition.IPhaseTransition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpellStateMachine {
    @Nullable
    private ISpellPhase currentPhase;
    private final SpellConfiguration config;
    private final List<ISpellPhaseListener> listeners;


    public SpellStateMachine(SpellConfiguration cfg) {
        this.currentPhase = cfg.getInitialPhase();
        this.config = cfg;
        this.listeners = new ArrayList<>();
    }

    /**
     * 在游戏 tick 循环中调用此方法以更新状态机，而不是渲染循环.
     * @param deltaTime 距离上一次调用该函数的时间间隔，单位为秒.
     */
    public void update(float deltaTime) {
        if (this.currentPhase != null) {
            this.currentPhase.update(this, deltaTime);
        }
    }

    public void addListener(ISpellPhaseListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ISpellPhaseListener listener) {
        this.listeners.remove(listener);
    }

    protected void notifyStateChanged(SpellPhaseType oldPhase, SpellPhaseType newPhase) {
        for (ISpellPhaseListener listener : this.listeners) {
            listener.onPhaseChanged(oldPhase, newPhase);
        }
    }

    private void handleTransition(String event) {
        if (this.currentPhase == null) {
            endSpell();
            return;
        }

        PhaseConfiguration currentPhaseConfig = this.config.getPhaseConfig(this.currentPhase.getPhaseType());
        if (currentPhaseConfig == null){
            throw new IllegalStateException("No phase configuration found for phase: " + this.currentPhase);
        }

        IPhaseTransition<SpellStateMachine> transition = currentPhaseConfig.getTransition(event);
        if (transition == null) {
            endSpell();
            return;
        }

        SpellPhaseType nextPhaseType = transition.decideNextPhase(this);
        if (nextPhaseType == null) {
            endSpell();
            return;
        }

        changePhase(nextPhaseType, currentPhaseConfig);
    }

    private void endSpell() {
        if (this.currentPhase != null) {
            this.currentPhase.onExit(this);
        }
        this.currentPhase = null;
        for (ISpellPhaseListener listener : this.listeners) {
            listener.onSpellCompleted();
        }
    }

    private void changePhase(SpellPhaseType newPhaseType, PhaseConfiguration currentPhaseConfig) {
        if (this.currentPhase != null) {
            this.currentPhase.onExit(this);
        }

        ISpellPhase oldPhase = this.currentPhase;
        this.currentPhase = createPhase(newPhaseType, currentPhaseConfig);
        this.currentPhase.onEnter(this);

        if (oldPhase == null) {
            Paramagic.LOG.error("Old phase is null when changing state.");
            return;
        }
        notifyStateChanged(oldPhase.getPhaseType(), newPhaseType);
    }

    private static ISpellPhase createPhase(SpellPhaseType newPhaseType, PhaseConfiguration currentPhaseConfig) {
        switch (newPhaseType) {
            case IDLE -> {
                return new IdlePhase(currentPhaseConfig);
            }
            case CASTING -> {
                return new CastingPhase(currentPhaseConfig);
            }
            // TODO: 实现其他的阶段类型
            default -> throw new IllegalArgumentException("Unknown phase type: " + newPhaseType);
        }
    }
}
