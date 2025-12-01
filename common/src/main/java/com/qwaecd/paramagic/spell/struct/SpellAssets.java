package com.qwaecd.paramagic.spell.struct;

import com.qwaecd.paramagic.data.animation.struct.AnimationBindingConfig;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpellAssets implements IDataSerializable {
    @Nonnull
    @Getter
    private final ParaData paraData;
    @Nullable
    @Getter
    private final AnimationBindingConfig animBindingConfig;

    public SpellAssets(@Nonnull ParaData paraData, @Nullable AnimationBindingConfig cfg) {
        this.paraData = paraData;
        this.animBindingConfig = cfg;
    }

    public SpellAssets(@Nonnull ParaData paraData) {
        this(paraData, null);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeObject("paraData", this.paraData);
        codec.writeObject("animBindingConfig", this.animBindingConfig);
    }

    public static SpellAssets fromCodec(DataCodec codec) {
        ParaData paraData = codec.readObject("paraData", ParaData::fromCodec);
        AnimationBindingConfig config = codec.readObject("animBindingConfig", AnimationBindingConfig::fromCodec);
        return new SpellAssets(paraData, config);
    }
}
