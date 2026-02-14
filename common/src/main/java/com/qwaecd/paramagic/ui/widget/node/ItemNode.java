package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.ui.MenuContent;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.MouseLeave;
import com.qwaecd.paramagic.ui.event.impl.MouseOver;
import com.qwaecd.paramagic.ui.util.UIColor;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemNode extends UINode {
    @Nonnull
    protected ItemStack renderingItem = ItemStack.EMPTY;
    public static final int CELL_SIZE = 16;

    protected boolean isHovering = false;

    protected final UIColor pendingColor = UIColor.of(173, 116, 40, 255);
    protected final int highLightColor = -2130706433;

    public ItemNode() {
        this.backgroundColor = UIColor.of(183, 126, 50, 255);
        this.localRect.setWH(CELL_SIZE, CELL_SIZE);
    }

    @Override
    protected void onMouseOver(UIEventContext<MouseOver> context) {
        this.isHovering = true;
        MenuContent menuContent = context.manager.getMenuContent();
        if (menuContent != null) {
            menuContent.setHoveringItemNode(this);
        }
    }

    @Override
    protected void onMouseLeave(UIEventContext<MouseLeave> context) {
        this.isHovering = false;
        MenuContent menuContent = context.manager.getMenuContent();
        if (menuContent != null) {
            menuContent.setHoveringItemNode(null);
        }
    }

    public void setRenderingItem(@Nullable ItemStack itemStack) {
        this.renderingItem = itemStack == null ? ItemStack.EMPTY : itemStack;
    }

    @Nonnull
    public ItemStack getRenderingItem() {
        return this.renderingItem;
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        super.render(context);
//        PoseStack view = RenderSystem.getModelViewStack();
//        view.pushPose();
//        float scale = 1.0f;
//        view.translate(worldRect.x + CELL_SIZE / 2.0f, worldRect.y + CELL_SIZE / 2.0f, 0.0f);
//        view.scale(scale, scale, 1.0f);
//        RenderSystem.applyModelViewMatrix();
//        context.renderItem(this.currentItem, (int) (-CELL_SIZE / 2.0f), (int) (-CELL_SIZE / 2.0f));
//        view.popPose();
//        RenderSystem.applyModelViewMatrix();
        context.renderItem(this.getRenderingItem(), (int) worldRect.x, (int) worldRect.y);
        if (this.isHovering) {
            this.renderSlotHighlight(context);
        }
    }

    protected void renderSlotHighlight(UIRenderContext context) {
        context.fill(this.worldRect.x, this.worldRect.y, this.worldRect.x + CELL_SIZE, this.worldRect.y + CELL_SIZE, this.highLightColor);
    }
}
