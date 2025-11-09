package com.qwaecd.paramagic.data;

import com.qwaecd.paramagic.data.animation.struct.AnimatorData;
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
    private final AnimatorData animatorData;

    public SpellAssets(@Nonnull ParaData paraData, @Nullable AnimatorData animatorData) {
        this.paraData = paraData;
        this.animatorData = animatorData;
    }

    public SpellAssets(@Nonnull ParaData paraData) {
        this(paraData, null);
    }
}
