package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import com.qwaecd.paramagic.spell.view.HybridCasterSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class ClientSession extends SpellSession implements ClientSessionView, AutoCloseable {
    @Nonnull
    protected final HybridCasterSource casterSource;

    public ClientSession(UUID sessionId, @Nonnull HybridCasterSource casterSource) {
        super(sessionId);
        this.casterSource = casterSource;
    }

    public abstract void tick(float deltaTime);

    public void upsertCasterSource(@Nonnull Entity source) {
        this.casterSource.setPrimary(source);
    }

    @Override
    public CasterTransformSource casterSource() {
        return this.casterSource;
    }

    @Override
    public int casterNetId() throws NullPointerException {
        return this.casterSource.getCasterNetId();
    }

    @Override
    public void close() {
    }
}
