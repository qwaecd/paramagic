package com.qwaecd.paramagic.client.render.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.core.render.things.BasePoseStack;
import com.qwaecd.paramagic.core.render.things.IPoseStack;

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
}
