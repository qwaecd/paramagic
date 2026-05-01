package com.qwaecd.paramagic.spell.core;

import com.qwaecd.paramagic.spell.core.store.SessionDataStore;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class SpellSession {
    @Getter
    protected final UUID sessionId;
    @Nonnull
    protected SessionState sessionState;

    @Getter
    protected final SessionDataStore dataStore = new SessionDataStore();

    private SpellSession(UUID sessionId) {
        this.sessionId = sessionId;
        this.sessionState = SessionState.RUNNING;
    }

    protected SpellSession() {
        this(UUID.randomUUID());
    }

    public void interrupt() {
        this.sessionState = SessionState.STOPPING;
    }

    public boolean canRemoveFromManager() {
        return this.isState(SessionState.DISPOSED);
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
