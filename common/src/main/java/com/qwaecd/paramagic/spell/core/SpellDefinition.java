package com.qwaecd.paramagic.spell.core;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.spell.config.SpellMetaConfig;
import com.qwaecd.paramagic.spell.config.phase.PhaseConfig;
import com.qwaecd.paramagic.spell.config.phase.PhaseSequenceConfig;

import javax.annotation.Nonnull;

@SuppressWarnings("ClassCanBeRecord")
public class SpellDefinition implements IDataSerializable {
    @Nonnull
    public final String spellId;
    @Nonnull
    public final SpellMetaConfig meta;
    @Nonnull
    public final PhaseSequenceConfig phases;

    public SpellDefinition(
            @Nonnull String spellId,
            @Nonnull SpellMetaConfig meta,
            @Nonnull PhaseSequenceConfig phases
    ) {
        this.spellId = spellId;
        this.meta = meta;
        this.phases = phases;
    }

    public static class Builder {
        private final String id;
        private PhaseSequenceConfig phases;

        public Builder(String id) {
            this.id = id;
        }

        public Builder addPhase(PhaseConfig cfg) {
            if (this.phases == null) {
                this.phases = new PhaseSequenceConfig(cfg);
            } else {
                this.phases.addPhaseConfig(cfg);
            }
            return this;
        }

        public SpellDefinition build(SpellMetaConfig meta) {
            return new SpellDefinition(id, meta, this.phases);
        }
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeString("spellId", this.spellId);
        codec.writeObject("meta", this.meta);
        codec.writeObject("phases", this.phases);
    }

    public SpellDefinition copy() {
        return new SpellDefinition(this.spellId, this.meta, this.phases);
    }

    public static SpellDefinition fromCodec(DataCodec codec) {
        String spellId = codec.readString("spellId");
        SpellMetaConfig meta = codec.readObject("meta", SpellMetaConfig::fromCodec);
        PhaseSequenceConfig phases = codec.readObject("phases", PhaseSequenceConfig::fromCodec);
        return new SpellDefinition(spellId, meta, phases);
    }
}
