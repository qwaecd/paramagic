package com.qwaecd.paramagic.core.render.context;

import com.qwaecd.paramagic.core.render.things.ICamera;
import com.qwaecd.paramagic.core.render.things.IMatrixStackProvider;
import org.joml.Matrix4f;

public interface RenderContext {

    ICamera getCamera();

    IMatrixStackProvider getMatrixStackProvider();

    Matrix4f getProjectionMatrix();

}
