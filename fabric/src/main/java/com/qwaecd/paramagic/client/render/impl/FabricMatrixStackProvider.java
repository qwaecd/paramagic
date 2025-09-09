package com.qwaecd.paramagic.client.render.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.core.render.things.IMatrixStackProvider;
import org.joml.Matrix4f;

public class FabricMatrixStackProvider implements IMatrixStackProvider {
    private final PoseStack mcPoseStack;

    public FabricMatrixStackProvider(PoseStack poseStack) {
        this.mcPoseStack = poseStack;
    }

    @Override
    public Matrix4f getViewMatrix() {
        return this.mcPoseStack.last().pose();
    }
}
