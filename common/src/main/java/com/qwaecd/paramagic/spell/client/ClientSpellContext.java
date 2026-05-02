package com.qwaecd.paramagic.spell.client;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.core.store.SessionDataStore;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;

import javax.annotation.Nonnull;

@PlatformScope(PlatformScopeType.CLIENT)
public class ClientSpellContext {
    @Nonnull
    private final ClientSession session;

    public ClientSpellContext(@Nonnull ClientSession session) {
        this.session = session;
    }

    @Nonnull
    public ClientSession getSession() {
        return this.session;
    }

    @Nonnull
    public SessionDataStore getDataStore() {
        return this.session.getDataStore();
    }

    @Nonnull
    public CasterTransformSource casterSource() {
        return this.session.casterSource();
    }

    public int casterNetId() {
        return this.session.casterNetId();
    }
}
