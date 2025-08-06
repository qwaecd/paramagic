package com.qwaecd.paramagic.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.core.render.RenderContext;
import lombok.Getter;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

@Getter
public class FabricRenderContext implements RenderContext {
    private final Camera camera;
    private final PoseStack poseStack;
    private final Matrix4f projectionMatrix;

    public FabricRenderContext(Camera camera, PoseStack poseStack, Matrix4f projectionMatrix) {
        this.camera = camera;
        this.poseStack = poseStack;
        this.projectionMatrix = projectionMatrix;
    }
}
