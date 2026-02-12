package com.qwaecd.paramagic.spell.builtin.client;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class VisualEntry {
    @Nonnull
    private final BuiltinSpellVisual visual;

    @Nonnull
    private final Supplier<SpellRenderer> rendererFactory;

    public VisualEntry(@Nonnull BuiltinSpellVisual visual, @Nonnull Supplier<SpellRenderer> factory) {
        this.visual = visual;
        this.rendererFactory = factory;
    }

    @Nonnull
    public BuiltinSpellVisual getVisual() {
        return this.visual;
    }

    @Nonnull
    public SpellRenderer createRenderer() {
        return this.rendererFactory.get();
    }
}
