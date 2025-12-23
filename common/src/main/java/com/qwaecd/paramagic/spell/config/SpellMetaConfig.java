package com.qwaecd.paramagic.spell.config;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import lombok.Getter;

import javax.annotation.Nonnull;

@SuppressWarnings("ClassCanBeRecord")
public class SpellMetaConfig implements IDataSerializable {
    @Nonnull
    @Getter
    public final SpellPhaseType executePhase;

    public SpellMetaConfig() {
        this.executePhase = SpellPhaseType.CHANNELING;
    }

    public SpellMetaConfig(@Nonnull SpellPhaseType executePhase) {
        this.executePhase = executePhase;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("executePhase", this.executePhase.ID());
    }

    public static SpellMetaConfig fromCodec(DataCodec codec) {
        int executePhaseId = codec.readInt("executePhase");
        SpellPhaseType executePhase = SpellPhaseType.fromID(executePhaseId);
        return new SpellMetaConfig(executePhase);
    }
}
