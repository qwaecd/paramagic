package com.qwaecd.paramagic.spell.core;

import com.qwaecd.paramagic.spell.core.store.SessionDataStore;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class SpellSession {
    @Getter
    protected final UUID sessionId;
    @Nonnull
    protected SessionState sessionState;
    @Nullable
    private EndSpellReason endReason;

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
        this.transitionToStopping(reason);
        this.onStopRequested(reason);
        return true;
    }

    protected abstract void onStopRequested(@Nonnull EndSpellReason reason);

    protected final boolean transitionToStopping(@Nonnull EndSpellReason reason) {
        if (!this.isState(SessionState.RUNNING)) {
            return false;
        }
        this.endReason = reason;
        this.sessionState = SessionState.STOPPING;
        return true;
    }

    protected final boolean markCompleted() {
        if (!this.transitionToStopping(EndSpellReason.COMPLETED)) {
            return false;
        }
        this.onCompleted();
        return true;
    }

    protected void onCompleted() {
    }

    public boolean canRemoveFromManager() {
        return this.isState(SessionState.DISPOSED);
    }

    public Optional<EndSpellReason> getEndReason() {
        return Optional.ofNullable(this.endReason);
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
