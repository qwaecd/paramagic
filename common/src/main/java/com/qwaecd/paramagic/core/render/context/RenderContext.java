package com.qwaecd.paramagic.core.render.context;

import com.qwaecd.paramagic.core.render.things.ICamera;
import com.qwaecd.paramagic.core.render.things.IPoseStack;
import org.joml.Matrix4f;

public interface RenderContext {

    ICamera getCamera();

    IPoseStack getPoseStack();

    Matrix4f getProjectionMatrix();

}
