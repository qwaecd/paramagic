package com.qwaecd.paramagic.spell.arcane;

import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.spell.server.ServerSpellContext;
import com.qwaecd.paramagic.spell.server.SpellRuntime;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.thaumaturgy.runtime.ArcaneProcessor;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArcaneRuntime implements SpellRuntime {
    @Nonnull
    private final ParaTree paraTree;
    @Nullable
    private ArcaneProcessor processor;
    private boolean finished = false;

    public ArcaneRuntime(@Nonnull ParaTree paraTree) {
        this.paraTree = paraTree;
    }

    @Override
    public void onStart(ServerSpellContext context) {
        this.finished = false;
        this.processor = new ArcaneProcessor(this.paraTree, new ParaContext(context));
        this.processor.init();
    }

    @Override
    public void tick(ServerSpellContext context) {
        if (this.finished || this.processor == null) {
            return;
        }
        this.processor.tick();
    }

    @Override
    public void interrupt(ServerSpellContext context, EndSpellReason reason) {
        this.finished = true;
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public void dispose(ServerSpellContext context) {
        this.processor = null;
    }
}
