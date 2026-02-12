package com.qwaecd.paramagic.ui.widget;

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
    protected ItemStack currentItem = ItemStack.EMPTY;
    public static final int CELL_SIZE = 16;

    protected final UIColor pendingColor = UIColor.of(173, 116, 40, 255);

    public ItemNode() {
        this.backgroundColor = UIColor.of(183, 126, 50, 255);
        this.localRect.setWH(CELL_SIZE, CELL_SIZE);
    }

    @Override
    protected void onMouseOver(UIEventContext<MouseOver> context) {
        MenuContent menuContent = context.manager.getMenuContent();
        if (menuContent != null) {
            menuContent.setHoveringItem(this.currentItem);
        }
    }

    @Override
    protected void onMouseLeave(UIEventContext<MouseLeave> context) {
        MenuContent menuContent = context.manager.getMenuContent();
        if (menuContent != null) {
            menuContent.setHoveringItem(null);
        }
    }

    public void putItem(@Nullable ItemStack itemStack) {
        this.currentItem = itemStack == null ? ItemStack.EMPTY : itemStack;
    }

    public ItemStack takeItem() {
        ItemStack item = this.currentItem;
        this.currentItem = ItemStack.EMPTY;
        return item;
    }

    @Nonnull
    public ItemStack getItem() {
        return this.currentItem;
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
        context.renderItem(this.currentItem, (int) worldRect.x, (int) worldRect.y);
    }
}
