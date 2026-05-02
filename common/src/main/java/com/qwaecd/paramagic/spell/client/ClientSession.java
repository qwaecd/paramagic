package com.qwaecd.paramagic.spell.client;

import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.spell.core.SessionState;
import com.qwaecd.paramagic.spell.core.SpellSession;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import com.qwaecd.paramagic.spell.view.HybridCasterSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ClientSession extends SpellSession implements AutoCloseable {
    @Nonnull
    private final HybridCasterSource casterSource;
    @Nonnull
    private final SpellPresentation presentation;
    @Nonnull
    private final ClientSpellContext context;
    private boolean started = false;

    public ClientSession(@Nonnull UUID sessionId, @Nonnull SpellPresentation presentation, @Nonnull HybridCasterSource casterSource) {
        super(sessionId);
        this.casterSource = casterSource;
        this.presentation = presentation;
        this.context = new ClientSpellContext(this);
    }

    public void tick() {
        if (!this.started) {
            this.start();
        }

        if (this.isState(SessionState.RUNNING)) {
            this.presentation.tick(this.context);
        }
        if (this.isState(SessionState.STOPPING)) {
            this.presentation.tick(this.context);
        }
        if (!this.isState(SessionState.DISPOSED) && this.presentation.canDispose()) {
            this.setSessionState(SessionState.DISPOSED);
        }
    }

    public void start() {
        if (this.started) {
            return;
        }
        this.started = true;
        this.presentation.onStart(this.context);
    }

    @Override
    protected void onStopRequested(@Nonnull EndSpellReason reason) {
        this.stopPresentation(reason);
    }

    public boolean handleServerStop(@Nonnull EndSpellReason reason) {
        if (!this.transitionToStopping(reason)) {
            return false;
        }
        this.stopPresentation(reason);
        return true;
    }

    private void stopPresentation(@Nonnull EndSpellReason reason) {
        this.presentation.onStop(this.context, reason);
        if (this.presentation.canDispose()) {
            this.setSessionState(SessionState.DISPOSED);
        }
    }

    @Override
    public boolean canRemoveFromManager() {
        return this.presentation.canDispose() && super.canRemoveFromManager();
    }

    public void upsertCasterSource(@Nonnull Entity source) {
        this.casterSource.setPrimary(source);
    }

    public CasterTransformSource casterSource() {
        return this.casterSource;
    }

    public int casterNetId() throws NullPointerException {
        return this.casterSource.getCasterNetId();
    }

    @Override
    public void close() {
        this.presentation.dispose(this.context);
    }
}
