package com.qwaecd.paramagic.client.renderbase;

import com.qwaecd.paramagic.core.render.IRenderable;
import lombok.Getter;

public class BaseObjectManager {
    @Getter
    private static IRenderable BASE_BALL;

    public static void init() {
        BASE_BALL = new BaseBall(32, 64);
    }

}
