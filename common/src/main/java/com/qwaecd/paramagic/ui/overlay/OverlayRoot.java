package com.qwaecd.paramagic.ui.overlay;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.UINode;

import javax.annotation.Nonnull;

public class OverlayRoot {
    @Nonnull
    private final UINode rootNode;
    public OverlayRoot() {
        this.rootNode = new UINode();
    }

    public void renderOverlay(UIRenderContext context) {
        this.rootNode.renderTree(context);
    }
}
