package com.qwaecd.paramagic.spell.config;

import com.qwaecd.paramagic.data.animation.struct.AnimationBindingConfig;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CircleAssets implements IDataSerializable {
    @Nonnull
    @Getter
    private final ParaData paraData;
    @Nullable
    @Getter
    private final AnimationBindingConfig animBindingConfig;

    public CircleAssets(@Nonnull ParaData paraData, @Nullable AnimationBindingConfig cfg) {
        this.paraData = paraData;
        this.animBindingConfig = cfg;
    }

    public CircleAssets(@Nonnull ParaData paraData) {
        this(paraData, null);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeObject("paraData", this.paraData);
        codec.writeObjectNullable("animBindingConfig", this.animBindingConfig);
    }

    public static CircleAssets fromCodec(DataCodec codec) {
        ParaData paraData = codec.readObject("paraData", ParaData::fromCodec);
        AnimationBindingConfig config = codec.readObjectNullable("animBindingConfig", AnimationBindingConfig::fromCodec);
        return new CircleAssets(paraData, config);
    }
}
