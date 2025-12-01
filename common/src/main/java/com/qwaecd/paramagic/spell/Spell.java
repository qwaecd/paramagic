package com.qwaecd.paramagic.spell;


import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import com.qwaecd.paramagic.spell.struct.SpellAssets;
import com.qwaecd.paramagic.spell.struct.SpellConfig;
import lombok.Getter;

import javax.annotation.Nonnull;

@SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
public class Spell implements IDataSerializable {
    @Getter
    private final String id;

    private final SpellConfig spellConfig;
    @Nonnull
    private final SpellAssets spellAssets;

    public Spell(String id, @Nonnull SpellAssets spellAssets, SpellConfig cfg) {
        this.id = id;
        this.spellAssets = spellAssets;
        this.spellConfig = cfg;
    }

    @Nonnull
    public SpellAssets getSpellAssets() {
        return this.spellAssets;
    }

    public SpellConfig getSpellConfig() {
        return this.spellConfig;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeString("id", this.id);
        codec.writeObject("spellConfig", this.spellConfig);
        codec.writeObject("spellAssets", this.spellAssets);
    }

    public static Spell fromCodec(DataCodec codec) {
        String id = codec.readString("id");
        SpellConfig spellConfig = codec.readObject("spellConfig", SpellConfig::fromCodec);
        SpellAssets spellAssets = codec.readObject("spellAssets", SpellAssets::fromCodec);
        return new Spell(id, spellAssets, spellConfig);
    }

    public Spell copy() {
        return new Spell(this.id, this.spellAssets, this.spellConfig);
    }

    public static class Builder {
        private SpellConfig spellConfig;
        private final String id;

        public Builder(String id) {
            this.id = id;
        }

        public Builder addPhase(PhaseConfig cfg) {
            if (this.spellConfig == null) {
                this.spellConfig = new SpellConfig(cfg);
            } else {
                this.spellConfig.addPhaseConfig(cfg);
            }
            return this;
        }

        public Spell build(SpellAssets spellAssets) {
            return new Spell(id, spellAssets, this.spellConfig);
        }
    }
}
