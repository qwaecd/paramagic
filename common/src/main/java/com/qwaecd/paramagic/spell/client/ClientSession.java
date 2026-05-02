package com.qwaecd.paramagic.spell.client;

import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.spell.core.SessionState;
import com.qwaecd.paramagic.spell.core.SpellSession;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import com.qwaecd.paramagic.spell.view.HybridCasterSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class ClientSession extends SpellSession implements AutoCloseable {
    @Nonnull
    protected final HybridCasterSource casterSource;
    @Nullable
    private final SpellPresentation presentation;
    @Nonnull
    private final ClientSpellContext context;
    private boolean started = false;

    public ClientSession(@Nonnull SpellPresentation presentation, @Nonnull HybridCasterSource casterSource) {
        this(UUID.randomUUID(), presentation, casterSource);
    }

    public ClientSession(@Nonnull UUID sessionId, @Nonnull SpellPresentation presentation, @Nonnull HybridCasterSource casterSource) {
        super(sessionId);
        this.casterSource = casterSource;
        this.presentation = presentation;
        this.context = new ClientSpellContext(this);
    }

    protected ClientSession(@Nonnull UUID sessionId, @Nonnull HybridCasterSource casterSource) {
        super(sessionId);
        this.casterSource = casterSource;
        this.presentation = null;
        this.context = new ClientSpellContext(this);
    }

    @Deprecated
    public void onTick(float deltaTime) {
    }

    public void tick() {
        if (!this.started) {
            this.start();
        }

        if (this.presentation != null) {
            if (this.isState(SessionState.RUNNING)) {
                this.presentation.tick(this.context);
            }
            if (this.presentation.canDispose()) {
                this.setSessionState(SessionState.DISPOSED);
            }
            return;
        }

        this.onTick(1.0f / 20.0f);
    }

    public void start() {
        if (this.started) {
            return;
        }
        this.started = true;
        if (this.presentation != null) {
            this.presentation.onStart(this.context);
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if (this.presentation != null) {
            this.presentation.onStop(this.context, EndSpellReason.INTERRUPTED);
            if (this.presentation.canDispose()) {
                this.setSessionState(SessionState.DISPOSED);
            }
        }
    }

    @Override
    public boolean canRemoveFromManager() {
        if (this.presentation == null) {
            return super.canRemoveFromManager();
        }
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
        if (this.presentation != null) {
            this.presentation.dispose(this.context);
        }
    }
}
