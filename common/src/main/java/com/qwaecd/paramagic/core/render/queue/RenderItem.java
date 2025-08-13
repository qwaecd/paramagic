package com.qwaecd.paramagic.core.render.queue;

import com.qwaecd.paramagic.core.render.IRenderable;
import org.joml.Vector3d;

public class RenderItem {
    public final IRenderable renderable;
    public final RenderType renderType;
    public final double distanceSq;


    public RenderItem(IRenderable renderable, RenderType renderType, Vector3d cameraPos) {
        this.renderable = renderable;
        this.renderType = renderType;
        var m = renderable.getTransform().getModelMatrix();
        double dx = m.m30() - cameraPos.x;
        double dy = m.m31() - cameraPos.y;
        double dz = m.m32() - cameraPos.z;
        this.distanceSq = dx*dx + dy*dy + dz*dz;
    }
}
