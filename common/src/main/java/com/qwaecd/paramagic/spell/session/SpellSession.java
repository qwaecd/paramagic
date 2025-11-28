package com.qwaecd.paramagic.spell.session;

import com.qwaecd.paramagic.spell.Spell;
import lombok.Getter;

import java.util.UUID;

public abstract class SpellSession {
    @Getter
    protected final UUID sessionId;
    @Getter
    protected final Spell spell;

    protected SpellSession(UUID sessionId, Spell spell) {
        this.sessionId = sessionId;
        this.spell = spell;
    }

    public abstract void tick(float deltaTime);

    public abstract void interrupt();

    public abstract void forceInterrupt();
}
