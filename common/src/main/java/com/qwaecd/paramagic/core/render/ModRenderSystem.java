package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.Constants;
import com.qwaecd.paramagic.core.render.buffer.BufferManager;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;

public class ModRenderSystem extends AbstractRenderSystem{



    public static void init() {
    }

    public static void initAfterClientStarted() {
        BufferManager.init();
        ShaderManager.init();
        Constants.LOG.info("Render system initialized.");
    }
}
