package com.qwaecd.paramagic.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public class GroupElement extends Element {
    public GroupElement() {
        // Groups don't render themselves, only their children
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, Vec3 centerPos, float partialTicks) {
        poseStack.pushPose();
        applyTransformations(poseStack);
        renderChildren(poseStack, buffer, centerPos, partialTicks);
        poseStack.popPose();
    }
}
