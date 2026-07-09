package com.qwaecd.paramagic.ui.api;

public final class UIRenderContextCache {
    private UIRenderContext renderContext;

    public UIRenderContextCache() {
    }

    public void setRenderContext(UIRenderContext context) {
        this.renderContext = context;
    }

    public UIRenderContext getRenderContext() {
        return this.renderContext;
    }
}
