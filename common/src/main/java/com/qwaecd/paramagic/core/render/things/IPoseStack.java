package com.qwaecd.paramagic.core.render.things;

public interface IPoseStack {
    @Deprecated
    IPoseStack getPoseStack();
    BasePoseStack.Pose getLastPose();
}
