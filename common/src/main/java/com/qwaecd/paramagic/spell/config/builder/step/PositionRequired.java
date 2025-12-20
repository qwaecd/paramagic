package com.qwaecd.paramagic.spell.config.builder.step;

import com.qwaecd.paramagic.spell.config.CircleTransformConfig;
import org.joml.Vector3f;

public interface PositionRequired {
    TransformRequired transformConfig(CircleTransformConfig cfg);
    default TransformRequired transformConfig(Vector3f scale, Vector3f rot) {
        return transformConfig(new CircleTransformConfig(scale, rot));
    }
}
