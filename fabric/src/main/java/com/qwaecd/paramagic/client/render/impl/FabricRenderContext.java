package com.qwaecd.paramagic.client.render.impl;

import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.things.ICamera;
import com.qwaecd.paramagic.core.render.things.IPoseStack;
import lombok.Getter;
import org.joml.Matrix4f;

@Getter
public class FabricRenderContext implements RenderContext {
    private final ICamera camera;
    private final IPoseStack poseStack;
    private final Matrix4f projectionMatrix;

    public FabricRenderContext(ICamera camera, IPoseStack poseStack, Matrix4f projectionMatrix) {
        this.camera = camera;
        this.poseStack = poseStack;
        this.projectionMatrix = projectionMatrix;
    }
}
