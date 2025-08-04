package com.qwaecd.paramagic.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.core.render.RenderContext;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

public class ForgeRenderContext implements RenderContext {
    @Override
    public Camera getCamera() {
        return null;
    }

    @Override
    public PoseStack getPoseStack() {
        return null;
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return null;
    }
}
