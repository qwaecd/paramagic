package com.qwaecd.paramagic.spell.session;

import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.listener.SpellPhaseListener;
import com.qwaecd.paramagic.spell.session.store.SessionDataStore;
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

    @Getter
    protected final SessionDataStore dataStore = new SessionDataStore();


    protected final List<SpellPhaseListener> listeners = new ArrayList<>();

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

    public void unregisterListener(SpellPhaseListener listener) {
        this.listeners.remove(listener);
    }

    public void registerListener(SpellPhaseListener listener) {
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
