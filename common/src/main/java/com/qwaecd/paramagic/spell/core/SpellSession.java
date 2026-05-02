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

    protected SpellSession(UUID sessionId) {
        this.sessionId = sessionId;
        this.sessionState = SessionState.RUNNING;
    }

    public final boolean interrupt() {
        return this.requestStop(EndSpellReason.INTERRUPTED);
    }

    public final boolean requestStop(@Nonnull EndSpellReason reason) {
        if (reason == EndSpellReason.COMPLETED || !this.isState(SessionState.RUNNING)) {
            return false;
        }
        this.sessionState = SessionState.STOPPING;
        this.onStopRequested(reason);
        return true;
    }

    protected abstract void onStopRequested(@Nonnull EndSpellReason reason);

    public boolean canRemoveFromManager() {
        return this.isState(SessionState.DISPOSED);
    }

    @Nonnull
    public SessionState getSessionState() {
        return this.sessionState;
    }

    protected void setSessionState(@Nonnull SessionState state) {
        this.sessionState = state;
    }

    public boolean isState(@Nonnull SessionState state) {
        return this.sessionState == state;
    }
}
