package com.qwaecd.paramagic.feature.effect.exposion;

import com.qwaecd.paramagic.data.SpellAssets;
import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.SpellConfiguration;

import javax.annotation.Nonnull;

public class ExplosionSpell extends Spell {
    private ExplosionSpell(String id, @Nonnull SpellAssets spellAssets, SpellConfiguration cfg) {
        super(id, spellAssets, cfg);
    }
}
