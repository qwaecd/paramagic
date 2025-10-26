package com.qwaecd.paramagic.feature.spell.state;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.feature.spell.SpellConfiguration;
import com.qwaecd.paramagic.feature.spell.state.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.feature.spell.state.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.feature.spell.state.phase.ISpellPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.PhaseConfiguration;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;
import com.qwaecd.paramagic.feature.spell.state.phase.struct.impl.CastingPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.struct.impl.IdlePhase;
import com.qwaecd.paramagic.feature.spell.state.transition.AllTransEvents;
import com.qwaecd.paramagic.feature.spell.state.transition.IPhaseTransition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("LombokGetterMayBeUsed")
public class SpellStateMachine {
    @Nullable
    private ISpellPhase currentPhase;
    private final SpellConfiguration spellConfiguration;
    private final List<ISpellPhaseListener> listeners;

    private boolean isCompleted = false;


    public SpellStateMachine(SpellConfiguration cfg) {
        this.spellConfiguration = cfg;
        this.listeners = new ArrayList<>();
        changePhase(cfg.getInitialPhase().getPhaseType());
    }

    /**
     * 在游戏 tick 循环中调用此方法以更新状态机，而不是渲染循环.
     * @param deltaTime 距离上一次调用该函数的时间间隔，单位为秒.
     */
    public void update(float deltaTime) {
        if (this.currentPhase != null) {
            this.currentPhase.update(this, deltaTime);

            for (ISpellPhaseListener listener : this.listeners) {
                listener.onTick(deltaTime);
            }
        } else {
            endSpell();
        }
    }

    public void requestNextPhase() {
        handleTransition(AllTransEvents.NEXT.get());
    }

    public void interrupt() {
        interrupt(AllTransEvents.INTERRUPT.get());
    }

    public void interrupt(String reason) {
        handleTransition(reason);
        for (ISpellPhaseListener listener : this.listeners) {
            listener.onSpellInterrupted();
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

    public void triggerEffect(EffectTriggerPoint triggerPoint) {
        switch (triggerPoint) {
            case ON_ENTER, ON_EXIT -> {
                for (ISpellPhaseListener listener : this.listeners) {
                    listener.onEffectTriggered(triggerPoint);
                }
            }
            default -> {
            }
        }

    }

    public boolean isCompleted() {
        return isCompleted;
    }

    private void handleTransition(String event) {
        if (this.currentPhase == null) {
            return;
        }

        PhaseConfiguration currentPhaseConfig = this.spellConfiguration.getPhaseConfig(this.currentPhase.getPhaseType());
        if (currentPhaseConfig == null){
            throw new IllegalStateException("No phase configuration found for phase: " + this.currentPhase);
        }

        IPhaseTransition<SpellStateMachine> transition = currentPhaseConfig.getTransition(event);
        if (transition == null) {
            Paramagic.LOG.error("No transition found for event: {} in phase: {}", event, this.currentPhase.getPhaseType());
            return;
        }

        SpellPhaseType nextPhaseType = transition.decideNextPhase(this);
        PhaseConfiguration nextCfg = this.spellConfiguration.getPhaseConfig(nextPhaseType);
        if (nextPhaseType == null || nextCfg == null) {
            Paramagic.LOG.error("No next phase or configuration found for transition on event: {}", event);
            return;
        }

        changePhase(nextPhaseType);
    }

    private void endSpell() {
        this.isCompleted = true;
        if (this.currentPhase != null) {
            this.currentPhase.onExit(this);
        }
        this.currentPhase = null;
        for (ISpellPhaseListener listener : this.listeners) {
            listener.onSpellCompleted();
        }
    }

    private void changePhase(SpellPhaseType newPhaseType) {
        if (this.currentPhase != null) {
            this.currentPhase.onExit(this);
        }

        ISpellPhase oldPhase = this.currentPhase;
        this.currentPhase = createPhaseFromConfig(this.spellConfiguration.getPhaseConfig(newPhaseType));
        this.currentPhase.onEnter(this);

        if (oldPhase != null) {
            notifyStateChanged(oldPhase.getPhaseType(), newPhaseType);
        }
    }

    private static ISpellPhase createPhaseFromConfig(PhaseConfiguration cfg) {
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
