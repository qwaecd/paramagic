package com.qwaecd.paramagic.spell;

import com.qwaecd.paramagic.data.animation.struct.AnimationBindingConfig;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpellAssets {
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
}
