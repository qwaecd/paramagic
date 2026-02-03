package com.qwaecd.paramagic.ui.overlay;

import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;

import javax.annotation.Nonnull;

public class OverlayRoot {
    @Nonnull
    private final UINode rootNode;
    @Nonnull
    private final UIManager manager;

    public OverlayRoot(@Nonnull UIManager manager) {
        this.rootNode = new UINode();
        this.manager = manager;
    }

    public void renderOverlay(UIRenderContext context) {
        this.rootNode.renderTree(context);
//        context.renderTooltip(context.mouseX, context.mouseY);
    }
}
