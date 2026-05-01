package com.qwaecd.paramagic.spell.client;

import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.spell.core.SessionState;
import com.qwaecd.paramagic.spell.core.SpellSession;
import com.qwaecd.paramagic.spell.session.client.ClientSessionView;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import com.qwaecd.paramagic.spell.view.HybridCasterSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;

public class ClientSession extends SpellSession implements ClientSessionView, AutoCloseable {
    @Nonnull
    protected final HybridCasterSource casterSource;
    @Nonnull
    private final SpellPresentation presentation;
    @Nonnull
    private final ClientSpellContext context;

    public ClientSession(@Nonnull SpellPresentation presentation, @Nonnull HybridCasterSource casterSource) {
        super();
        this.casterSource = casterSource;
        this.presentation = presentation;
        this.context = new ClientSpellContext();
    }

    @Deprecated
    public void onTick(float deltaTime) {
    }

    public void tick() {
        this.presentation.tick(this.context);
    }

    public void start() {
        if (this.sessionState == SessionState.RUNNING) {
            return;
        }
        this.presentation.onStart(this.context);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.presentation.onStop(this.context, EndSpellReason.INTERRUPTED);
    }

    @Override
    public boolean canRemoveFromManager() {
        return this.presentation.canDispose() && super.canRemoveFromManager();
    }

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
        this.presentation.dispose(this.context);
    }
}
