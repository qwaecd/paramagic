package com.qwaecd.paramagic.spell;


import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import lombok.Getter;

import javax.annotation.Nonnull;

@SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
public class Spell {
    @Getter
    private final String id;

    private final SpellConfiguration spellConfig;
    @Nonnull
    private final SpellAssets spellAssets;

    public Spell(String id, @Nonnull SpellAssets spellAssets, SpellConfiguration cfg) {
        this.id = id;
        this.spellAssets = spellAssets;
        this.spellConfig = cfg;
    }

    @Nonnull
    public SpellAssets getSpellAssets() {
        return this.spellAssets;
    }

    public SpellConfiguration getSpellConfig() {
        return this.spellConfig;
    }

    public static class Builder {
        private SpellConfiguration spellConfiguration;
        private final String id;

        public Builder(String id) {
            this.id = id;
        }

        public Builder addPhase(PhaseConfig cfg) {
            if (this.spellConfiguration == null) {
                this.spellConfiguration = new SpellConfiguration(cfg);
            } else {
                this.spellConfiguration.addPhaseConfig(cfg);
            }
            return this;
        }

        public Spell build(SpellAssets spellAssets) {
            return new Spell(id, spellAssets, this.spellConfiguration);
        }
    }
}
