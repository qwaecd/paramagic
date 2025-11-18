package com.qwaecd.paramagic.spell.state;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.spell.EndSpellReason;
import com.qwaecd.paramagic.spell.SpellConfig;
import com.qwaecd.paramagic.spell.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.spell.state.event.AllMachineEvents;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.spell.state.event.queue.EventQueue;
import com.qwaecd.paramagic.spell.state.event.queue.MachineEventEnvelope;
import com.qwaecd.paramagic.spell.state.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("LombokGetterMayBeUsed")
public class SpellStateMachine {
    public static final int MAX_EVENTS_PER_TICK = 64;
    @Nullable
    private SpellPhase currentPhase;
    private final SpellConfig spellConfig;
    private final List<ISpellPhaseListener> listeners;
    private static final SystemEvents systemEvents = new SystemEvents();
    /**
     * 当状态切换时, phaseGeneration 会增加 1.
     */
    private long phaseGeneration = 0L;

    private boolean isCompleted = false;
    private final EventQueue eventQueue = new EventQueue();
    private final MachineContext context;

    public SpellStateMachine(SpellConfig cfg) {
        this.spellConfig = cfg;
        this.listeners = new ArrayList<>();
        this.context = new MachineContext(this);
        changePhase(cfg.getInitialPhase());
    }

    /**
     * 在游戏 tick 循环中调用此方法以更新状态机，而不是渲染循环.
     * @param deltaTime 距离上一次调用该函数的时间间隔，单位为秒.
     */
    public void update(float deltaTime) {
        int processed = 0;
        while(processed < MAX_EVENTS_PER_TICK) {
            MachineEventEnvelope envelope = this.eventQueue.pollOne();
            if (envelope == null) {
                break;
            }
            if (envelope.getGeneration() != -1L && envelope.getGeneration() != this.phaseGeneration) {
                // 该事件已过期, 忽略
                continue;
            }
            processEvent(envelope.getEvent());
            processed++;
        }
        if (processed >= MAX_EVENTS_PER_TICK) {
            // 多出的事件不做处理
            Paramagic.LOG.warn("SpellStateMachine processed max events per tick ({}), remaining events will be discarded.", MAX_EVENTS_PER_TICK);
//            this.eventQueue.clear();
        }

        if (this.currentPhase != null && !isCompleted()) {
            this.currentPhase.update(this.context, deltaTime);

            final SpellPhaseType phaseType = this.currentPhase.getPhaseType();
            forEachListenerSafe(listener -> listener.onTick(phaseType, deltaTime));
        } else {
            endSpell(EndSpellReason.COMPLETED);
        }
    }

    private void processEvent(MachineEvent event) {
        if (systemEvents.contains(event)) {
            processSystemEvent(event);
            return;
        }

        if (this.currentPhase != null) {
            Transition transition = this.currentPhase.onEvent(this.context, event);
            if (transition != null && transition.getTargetPhase() != null) {
                // 进行状态转换
                handleTransition(transition);
            }
        }
    }

    private void processSystemEvent(MachineEvent event) {
        if (event.equals(AllMachineEvents.INTERRUPT)) {
            endSpell(EndSpellReason.INTERRUPTED);
        }
        if (event.equals(AllMachineEvents.END_SPELL)) {
            endSpell(EndSpellReason.COMPLETED);
        }
    }

    public void postEvent(MachineEvent event) {
        postEventBounded(event, true);
    }

    public void postEventBounded(MachineEvent event, boolean bindToPhase) {
        long gen = bindToPhase ? this.phaseGeneration : -1L;
        this.eventQueue.offer(event, gen);
    }

    public void interrupt() {
        postEventBounded(AllMachineEvents.INTERRUPT, false);
    }

    public void forceInterrupt() {
        this.endSpell(EndSpellReason.INTERRUPTED);
    }

    public void addListener(ISpellPhaseListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ISpellPhaseListener listener) {
        this.listeners.remove(listener);
    }

    public void triggerEffect(EffectTriggerPoint triggerPoint) {
        switch (triggerPoint) {
            case ON_ENTER, ON_EXIT ->
                    forEachListenerSafe(listener -> listener.onEffectTriggered(triggerPoint));
            default -> {
            }
        }

    }

    public boolean isCompleted() {
        return isCompleted;
    }

    private void notifyStateChanged(SpellPhaseType oldPhase, SpellPhaseType newPhase) {
        forEachListenerSafe(listener -> listener.onPhaseChanged(oldPhase, newPhase));
    }

    private void handleTransition(@Nonnull Transition transition) {
        if (this.currentPhase == null) {
            // 说明状态机理论上应该早被中断或者停止了
            return;
        }

        SpellPhaseType targetPhase = transition.getTargetPhase();
        // 当前阶段并没有返回转换到下一个状态的具体状态, 不应该发生转换
        if (targetPhase == null) return;

        SpellPhase newPhase = this.spellConfig.getPhase(targetPhase);
        if (newPhase == null) {
            throw new NullPointerException("No phase found for type: " + targetPhase);
        }

        changePhase(newPhase);
    }

    private void endSpell(EndSpellReason reason) {
        this.isCompleted = true;
        if (this.currentPhase != null) {
            this.currentPhase.onExit(this.context);
        }
        this.currentPhase = null;

        switch (reason) {
            case COMPLETED: {
                forEachListenerSafe(ISpellPhaseListener::onSpellCompleted);
                break;
            }
            case INTERRUPTED:
            case FAILED:
            default: {
                forEachListenerSafe(ISpellPhaseListener::onSpellInterrupted);
                break;
            }
        }
        this.eventQueue.clear();
    }

    private void changePhase(SpellPhase newPhase) {
        if (this.currentPhase != null) {
            this.currentPhase.onExit(this.context);
        }

        SpellPhase oldPhase = this.currentPhase;
        this.currentPhase = newPhase;
        this.phaseGeneration++;

        this.currentPhase.onEnter(this.context);

        if (oldPhase != null) {
            notifyStateChanged(oldPhase.getPhaseType(), newPhase.getPhaseType());
        }
    }

    private void forEachListenerSafe(Consumer<ISpellPhaseListener> action) {
        for (ISpellPhaseListener listener : this.listeners) {
            try {
                action.accept(listener);
            } catch (Exception e) {
                Paramagic.LOG.error("Error while notifying listener '{}' :", listener, e);
            }
        }
    }

    public static class SystemEvents {
        private final Set<MachineEvent> systemEvents = new HashSet<>();
        private SystemEvents() {
            systemEvents.add(AllMachineEvents.INTERRUPT);
            systemEvents.add(AllMachineEvents.END_SPELL);
        }

        public boolean contains(MachineEvent event) {
            return systemEvents.contains(event);
        }
    }
}
