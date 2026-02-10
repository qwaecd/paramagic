package com.qwaecd.paramagic.spell.core;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpell;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpellRegistry;
import com.qwaecd.paramagic.spell.logic.ExecutionContext;
import lombok.Getter;

import javax.annotation.Nonnull;


@SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression", "ClassCanBeRecord"})
public class Spell {
    @Getter
    @Nonnull
    public final SpellDefinition definition;

    public final boolean isBuiltIn;

    public Spell(@Nonnull SpellDefinition definition) {
        this.definition = definition;
        this.isBuiltIn = false;
    }

    public Spell(@Nonnull SpellDefinition definition, boolean isBuiltIn) {
        this.definition = definition;
        this.isBuiltIn = isBuiltIn;
    }

    public static Spell create(SpellDefinition definition) {
        return new Spell(definition);
    }

    public boolean isBuiltIn() {
        return this.isBuiltIn;
    }

    public void execute(ExecutionContext context) {
        if (this.isBuiltIn) {
            BuiltinSpell builtinSpell = BuiltinSpellRegistry.getSpell(this.definition.spellId);
            if (builtinSpell != null) {
                builtinSpell.execute(context);
            } else {
                Paramagic.LOG.error("Cannot find built-in spell with id: {}", this.definition.spellId);
            }
            return;
        }
//        ArcaneProcessor processor = new ArcaneProcessor(this.definition.logic);
//        processor.process(context);
    }
}
