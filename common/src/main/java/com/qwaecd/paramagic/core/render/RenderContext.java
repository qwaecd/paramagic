package com.qwaecd.paramagic.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

public interface RenderContext {

    Camera getCamera();

    PoseStack getPoseStack();

    Matrix4f getProjectionMatrix();

}
