package com.qwaecd.paramagic.ui.overlay;

import com.qwaecd.paramagic.ui.MenuContent;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.widget.node.ItemNode;
import net.minecraft.world.item.ItemStack;

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

        MenuContent menuContent = this.manager.getMenuContent();
        if (menuContent == null) {
            return;
        }

        ItemNode hovering = menuContent.getHoveringItemNode();
        if (hovering != null) {
            ItemStack item = hovering.getRenderingItem();
            if (!item.isEmpty()) {
                context.renderTooltipWithItem(item, context.mouseX, context.mouseY);
            }
        }
    }
}
