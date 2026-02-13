package com.qwaecd.paramagic.spell.util.transform;

import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.TransformSample;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import org.joml.Vector3f;

import java.util.function.Function;

public class FollowCasterFunction {
    private final Function<TransformSample, Vector3f> sourceProvider;

    private final TransformSample tempSample = new TransformSample();

    public FollowCasterFunction(Function<TransformSample, Vector3f> function) {
        this.sourceProvider = function;
    }

    public void apply(Transform item, TransformSample sample) {
        item.setPosition(this.sourceProvider.apply(sample));
    }

    public void apply(Transform item, CasterTransformSource source) {
        this.apply(item, source.applyTo(this.tempSample));
    }
}
