package com.qwaecd.paramagic.spell.core;

import javax.annotation.Nonnull;

@SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression", "ClassCanBeRecord"})
public class Spell {
    @Nonnull
    public final SpellDefinition definition;

    public Spell(@Nonnull SpellDefinition definition) {
        this.definition = definition;
    }

    public static Spell create(SpellDefinition definition) {
        return new Spell(definition);
    }
}
