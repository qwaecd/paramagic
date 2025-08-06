package com.qwaecd.paramagic.client.render.impl;

import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.things.ICamera;
import com.qwaecd.paramagic.core.render.things.IPoseStack;
import lombok.Getter;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import org.joml.Matrix4f;

@Getter
public class FabricRenderContext implements RenderContext {
    private final ICamera camera;
    private final IPoseStack poseStack;
    private final Matrix4f projectionMatrix;

    public FabricRenderContext(WorldRenderContext context) {
        this.camera = new FabricCamera(context.camera());
        this.poseStack = new FabricPoseStack(context.matrixStack());
        this.projectionMatrix = context.projectionMatrix();
    }
}
