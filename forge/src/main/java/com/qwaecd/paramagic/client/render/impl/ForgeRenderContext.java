package com.qwaecd.paramagic.client.render.impl;

import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.things.ICamera;
import com.qwaecd.paramagic.core.render.things.IPoseStack;
import org.joml.Matrix4f;

public class ForgeRenderContext implements RenderContext {
    @Override
    public ICamera getCamera() {
        return null;
    }

    @Override
    public IPoseStack getPoseStack() {
        return null;
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return null;
    }
}
