package com.qwaecd.paramagic.feature;


import com.qwaecd.paramagic.client.render.MagicCircleRenderer;
import org.joml.Matrix4f;

public class MagicCircle extends MagicNode {
    public MagicCircle() {
        super(null, null);
    }

    @Override
    public void addChild(MagicNode child) {
        if (child == null) {
            throw new IllegalArgumentException("MagicNode cannot be null.");
        }
        if (child instanceof MagicCircle) {
            throw new IllegalArgumentException("Cannot add a MagicCircle as a child to another MagicCircle.");
        }
        super.addChild(child);
    }

    @Override
    public void draw(Matrix4f parentWorldTransform, MagicCircleRenderer renderer) {
        super.draw(parentWorldTransform, renderer);
    }
}
