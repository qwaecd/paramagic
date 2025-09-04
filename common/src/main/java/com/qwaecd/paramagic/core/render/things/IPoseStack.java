package com.qwaecd.paramagic.core.render.things;


import org.joml.Matrix4f;

public interface IPoseStack {
    IPoseStack getPoseStack();
    BasePoseStack.Pose getLastPose();
    Matrix4f getViewMatrix();
}
