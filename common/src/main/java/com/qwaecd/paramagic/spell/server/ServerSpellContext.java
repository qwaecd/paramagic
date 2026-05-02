package com.qwaecd.paramagic.spell.server;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.spell.core.store.SessionDataStore;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;

public final class ServerSpellContext {
    @Nonnull
    private final ServerSession session;

    public ServerSpellContext(@Nonnull ServerSession session) {
        this.session = session;
    }

    @Nonnull
    public ServerSession getSession() {
        return this.session;
    }

    @Nonnull
    public EndSpellReason defaultStopReason() {
        return EndSpellReason.COMPLETED;
    }

    @Nonnull
    public ServerLevel getLevel() {
        return this.session.getLevel();
    }

    @Nonnull
    public SpellCaster getCaster() {
        return this.session.getCaster();
    }

    @Nonnull
    public SessionDataStore getDataStore() {
        return this.session.getDataStore();
    }
}
