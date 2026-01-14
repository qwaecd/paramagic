package com.qwaecd.paramagic.spell.view.position;

import com.qwaecd.paramagic.core.render.TransformSample;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import lombok.Setter;
import org.joml.Quaternionf;

@SuppressWarnings("LombokSetterMayBeUsed")
public class PositionRuleContext {
    private final MagicCircle circle;
    private TransformSample casterTransform;
    private final PositionRuleSpec spec;
    private final Temp temp = new Temp();

    public PositionRuleContext(
            MagicCircle circle,
            TransformSample casterTransform,
            PositionRuleSpec spec
    ) {
        this.circle = circle;
        this.casterTransform = casterTransform;
        this.spec = spec;
    }

    public MagicCircle circle() {
        return circle;
    }

    public TransformSample casterTransform() {
        return casterTransform;
    }

    public void setCasterTransform(TransformSample casterTransform) {
        this.casterTransform = casterTransform;
    }

    public PositionRuleSpec spec() {
        return spec;
    }

    public Quaternionf tempQuat() {
        return temp.quat;
    }

    private static class Temp {
        private final Quaternionf quat = new Quaternionf();
    }
}
