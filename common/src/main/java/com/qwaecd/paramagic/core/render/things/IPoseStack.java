package com.qwaecd.paramagic.core.render.things;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

public interface IPoseStack {
    IPoseStack getPoseStack();
    BasePoseStack.Pose getLastPose();

    void pushPose();
    void popPose();
    void mulPoseMatrix(Matrix4f matrix4f);
    void scale(float x, float y, float z);
    void translate(float x, float y, float z);
    void mulPose(Quaternionf quaternionf);
}
