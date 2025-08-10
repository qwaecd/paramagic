package com.qwaecd.paramagic.client.render.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.core.render.things.BasePoseStack;
import com.qwaecd.paramagic.core.render.things.IPoseStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class FabricPoseStack implements IPoseStack {
    private final PoseStack mcPoseStack;

    public FabricPoseStack(PoseStack poseStack) {
        this.mcPoseStack = poseStack;
    }

    @Override
    public FabricPoseStack getPoseStack() {
        return this;
    }

    @Override
    public BasePoseStack.Pose getLastPose() {
        PoseStack.Pose last = this.mcPoseStack.last();
        return new BasePoseStack.Pose(last.pose(), last.normal());
    }

    @Override
    public void pushPose() {
        mcPoseStack.pushPose();
    }

    @Override
    public void popPose() {
        mcPoseStack.popPose();
    }

    @Override
    public void mulPoseMatrix(Matrix4f matrix4f) {
        mcPoseStack.mulPoseMatrix(matrix4f);
    }

    @Override
    public void scale(float x, float y, float z) {
        mcPoseStack.scale(x, y, z);
    }

    @Override
    public void translate(float x, float y, float z) {
        mcPoseStack.translate(x, y, z);
    }

    @Override
    public void mulPose(Quaternionf quaternionf) {
        mcPoseStack.mulPose(quaternionf);
    }
}
