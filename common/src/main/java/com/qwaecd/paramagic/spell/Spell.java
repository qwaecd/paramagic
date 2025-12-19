package com.qwaecd.paramagic.spell;


import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.spell.state.phase.struct.PhaseConfig;
import com.qwaecd.paramagic.spell.struct.SpellConfig;
import lombok.Getter;

import javax.annotation.Nonnull;


@SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
public class Spell implements IDataSerializable {
    @Getter
    private final String id;

    @Nonnull
    private final SpellConfig spellConfig;

    public Spell(String id, @Nonnull SpellConfig cfg) {
        this.id = id;
        this.spellConfig = cfg;
    }

    @Nonnull
    public SpellConfig getSpellConfig() {
        return this.spellConfig;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeString("id", this.id);
        codec.writeObject("spellConfig", this.spellConfig);
    }

    public static Spell fromCodec(DataCodec codec) {
        String id = codec.readString("id");
        SpellConfig spellConfig = codec.readObject("spellConfig", SpellConfig::fromCodec);
        return new Spell(id, spellConfig);
    }

    public Spell copy() {
        return new Spell(this.id, this.spellConfig);
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

        public Spell build() {
            return new Spell(id, this.spellConfig);
        }
    }
}
