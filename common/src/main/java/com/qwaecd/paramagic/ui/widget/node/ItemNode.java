package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.ui.api.TooltipContent;
import com.qwaecd.paramagic.ui.api.TooltipQuery;
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

    protected final int highLightColor = -2130706433;

    public ItemNode() {
        this.backgroundColor = UIColor.of(183, 126, 50, 255);
        this.layoutRect.setWH(CELL_SIZE, CELL_SIZE);
    }

    @Override
    protected void onMouseOver(UIEventContext<MouseOver> context) {
        this.isHovering = true;
    }

    @Override
    protected void onMouseLeave(UIEventContext<MouseLeave> context) {
        this.isHovering = false;
    }

    public void setRenderingItem(@Nullable ItemStack itemStack) {
        this.renderingItem = itemStack == null ? ItemStack.EMPTY : itemStack;
    }

    @Nonnull
    public ItemStack getRenderingItem() {
        return this.renderingItem;
    }

    @Override
    @Nullable
    public TooltipContent getTooltip(@Nonnull TooltipQuery query) {
        ItemStack item = this.getRenderingItem();
        if (item.isEmpty()) {
            return null;
        }
        return UINode.getTooltipFromItem(item);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        super.render(context);
//        PoseStack view = RenderSystem.getModelViewStack();
//        view.pushPose();
//        float scale = 1.0f;
//        view.translate(finalRect.x + CELL_SIZE / 2.0f, finalRect.y + CELL_SIZE / 2.0f, 0.0f);
//        view.scale(scale, scale, 1.0f);
//        RenderSystem.applyModelViewMatrix();
//        context.renderItem(this.currentItem, (int) (-CELL_SIZE / 2.0f), (int) (-CELL_SIZE / 2.0f));
//        view.popPose();
//        RenderSystem.applyModelViewMatrix();
        context.renderItem(this.getRenderingItem(), (int) this.finalRect.x, (int) this.finalRect.y);
        context.renderItemDecorations(this.getRenderingItem(), (int) this.finalRect.x, (int) this.finalRect.y);
        if (this.isHovering) {
            this.renderSlotHighlight(context);
        }
    }

    @Override
    protected void renderBackGround(UIRenderContext context) {
    }

    protected void renderSlotHighlight(UIRenderContext context) {
        context.fillBounds(this.finalRect.x, this.finalRect.y, this.finalRect.x + CELL_SIZE, this.finalRect.y + CELL_SIZE, this.highLightColor);
    }
}
