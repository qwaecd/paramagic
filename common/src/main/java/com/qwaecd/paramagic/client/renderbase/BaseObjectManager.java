package com.qwaecd.paramagic.client.renderbase;

import com.qwaecd.paramagic.core.render.IRenderable;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import lombok.Getter;

public class BaseObjectManager {
    @Getter
    private static IRenderable BASE_BALL;
    @Getter
    private static IRenderable BASE_BALL_IN;
    @Getter
    private static IRenderable BASE_BALL_OUT;

    public static void init() {
        BASE_BALL = new BaseBall(32, 64);
        BASE_BALL_IN = new BaseBall(32, 64, ShaderManager.getBaseBallInShader());
        BASE_BALL_OUT = new BaseBall(32, 64, ShaderManager.getBaseBallOutShader());
    }

}
