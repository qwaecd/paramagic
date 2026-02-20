package com.qwaecd.paramagic.spell.util.transform;

import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.TransformSample;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;

import javax.annotation.Nonnull;

public class LambdaFunction {
    private final TransformSample tempSample = new TransformSample();
    @Nonnull
    private final Modifier modifier;

    public LambdaFunction(@Nonnull Modifier modifier) {
        this.modifier = modifier;
    }

    public void apply(Transform item, CasterTransformSource source) {
        this.modifier.modify(item, source, this.tempSample);
    }

    public interface Modifier {
        void modify(Transform item, CasterTransformSource source, TransformSample tempSample);
    }
}
