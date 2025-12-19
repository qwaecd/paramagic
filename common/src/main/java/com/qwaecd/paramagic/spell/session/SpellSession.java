package com.qwaecd.paramagic.spell.session;

import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class SpellSession {
    @Getter
    protected final UUID sessionId;
    @Getter
    protected final Spell spell;
    @Nonnull
    protected SessionState sessionState;

    protected final List<ISpellPhaseListener> listeners = new ArrayList<>();

    protected SpellSession(UUID sessionId, Spell spell) {
        this.sessionId = sessionId;
        this.spell = spell;
        this.sessionState = SessionState.RUNNING;
    }

    public abstract void interrupt();

    public abstract void forceInterrupt();

    public abstract void postEvent(MachineEvent event);

    public boolean canRemoveFromManager() {
        return this.isState(SessionState.DISPOSED);
    }

    public void unregisterListener(ISpellPhaseListener listener) {
        this.listeners.remove(listener);
    }

    public void registerListener(ISpellPhaseListener listener) {
        this.listeners.add(listener);
    }

    @Nonnull
    public SessionState getSessionState() {
        return this.sessionState;
    }

    public void setSessionState(@Nonnull SessionState state) {
        this.sessionState = state;
    }

    public boolean isState(@Nonnull SessionState state) {
        return this.sessionState == state;
    }
}
