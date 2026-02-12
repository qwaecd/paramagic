package com.qwaecd.paramagic.spell.builtin;

import com.qwaecd.paramagic.spell.session.server.SpellExecutor;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class BuiltinSpellEntry {
    @Nonnull
    private final BuiltinSpell spell;
    @Nonnull
    private final Supplier<SpellExecutor> executorFactory;

    BuiltinSpellEntry(@Nonnull BuiltinSpell spell, @Nonnull Supplier<SpellExecutor> executorFactory) {
        this.spell = spell;
        this.executorFactory = executorFactory;
    }

    @Nonnull
    public BuiltinSpell getSpell() {
        return this.spell;
    }

    @Nonnull
    public SpellExecutor createExecutor() {
        return this.executorFactory.get();
    }
}
