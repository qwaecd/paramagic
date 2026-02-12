package com.qwaecd.paramagic.spell.view.transform;

import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.TransformSample;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public class FixedAnchorFunction {
    private final Vector3f anchorPos;

    private final TransformSample tempSample = new TransformSample();

    public FixedAnchorFunction(@Nonnull Vector3f anchorPos) {
        this.anchorPos = anchorPos;
    }

    public void setAnchorPos(@Nonnull Vector3f v) {
        this.anchorPos.set(v);
    }

    public void setAnchorPos(float x, float y, float z) {
        this.anchorPos.set(x, y, z);
    }

    public Vector3f getAnchorPos(Vector3f dist) {
        return dist.set(this.anchorPos);
    }

    public void apply(Transform item) {
        item.setPosition(this.anchorPos);
    }
}
