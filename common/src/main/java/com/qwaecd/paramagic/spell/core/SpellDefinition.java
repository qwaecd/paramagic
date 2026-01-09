package com.qwaecd.paramagic.spell.core;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.spell.SpellIdentifier;
import com.qwaecd.paramagic.spell.config.SpellMetaConfig;
import com.qwaecd.paramagic.spell.config.phase.PhaseSequenceConfig;
import com.qwaecd.paramagic.spell.logic.SpellLogic;
import lombok.Getter;

import javax.annotation.Nonnull;

public class SpellDefinition implements IDataSerializable {
    @Getter
    @Nonnull
    public final SpellIdentifier spellId;
    @Getter
    @Nonnull
    public final SpellMetaConfig meta;
    @Getter
    @Nonnull
    public final PhaseSequenceConfig phases;

    @Getter
    @Nonnull
    public final SpellLogic logic;

    public SpellDefinition(
            @Nonnull SpellIdentifier spellId,
            @Nonnull SpellMetaConfig meta,
            @Nonnull PhaseSequenceConfig phases
    ) {
        this.spellId = spellId;
        this.meta = meta;
        this.phases = phases;
        this.logic = new SpellLogic();
    }

    public SpellDefinition(
            @Nonnull SpellIdentifier spellId,
            @Nonnull SpellMetaConfig meta,
            @Nonnull PhaseSequenceConfig phases,
            @Nonnull SpellLogic logic
    ) {
        this.spellId = spellId;
        this.meta = meta;
        this.phases = phases;
        this.logic = logic;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeObject("spellId", this.spellId);
        codec.writeObject("meta", this.meta);
        codec.writeObject("phases", this.phases);
        codec.writeObject("logic", this.logic);
    }

    public SpellDefinition copy() {
        return new SpellDefinition(this.spellId, this.meta, this.phases, this.logic);
    }

    public static SpellDefinition fromCodec(DataCodec codec) {
        SpellIdentifier spellId     = codec.readObject("spellId",   SpellIdentifier::fromCodec);
        SpellMetaConfig meta        = codec.readObject("meta",      SpellMetaConfig::fromCodec);
        PhaseSequenceConfig phases  = codec.readObject("phases",    PhaseSequenceConfig::fromCodec);
        SpellLogic logic            = codec.readObject("logic",     SpellLogic::fromCodec);
        return new SpellDefinition(spellId, meta, phases, logic);
    }
}
