package com.qwaecd.paramagic.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.core.render.RenderContext;
import lombok.Setter;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

public class FabricRenderContext implements RenderContext {
    public static RenderContext INSTANCE = new FabricRenderContext(null, null, null);
    @Setter
    private static Camera camera;
    @Setter
    private static PoseStack poseStack;
    @Setter
    private static Matrix4f projectionMatrix;

    public FabricRenderContext(Camera camera, PoseStack poseStack, Matrix4f projectionMatrix) {
        FabricRenderContext.camera = camera;
        FabricRenderContext.poseStack = poseStack;
        FabricRenderContext.projectionMatrix = projectionMatrix;
    }


    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public PoseStack getPoseStack() {
        return poseStack;
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
