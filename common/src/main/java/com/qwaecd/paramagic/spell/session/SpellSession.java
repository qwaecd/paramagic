package com.qwaecd.paramagic.spell.session;

import com.qwaecd.paramagic.spell.Spell;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class SpellSession {
    @Getter
    protected final UUID sessionId;
    @Getter
    protected final Spell spell;
    @Nonnull
    protected SessionState sessionState;

    protected SpellSession(UUID sessionId, Spell spell) {
        this.sessionId = sessionId;
        this.spell = spell;
        this.sessionState = SessionState.RUNNING;
    }

    public abstract void tick(float deltaTime);

    public abstract void interrupt();

    public abstract void forceInterrupt();

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
