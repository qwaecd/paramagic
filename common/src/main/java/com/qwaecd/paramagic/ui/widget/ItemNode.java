package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import com.qwaecd.paramagic.ui.event.api.UIEventContext;
import com.qwaecd.paramagic.ui.event.impl.MouseLeave;
import com.qwaecd.paramagic.ui.event.impl.MouseOver;
import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemNode extends UINode {
    private final ItemStack testItem = new ItemStack(ModItems.EXPLOSION_WAND);
    @Nonnull
    protected ItemStack currentItem = ItemStack.EMPTY;
    public static final int CELL_SIZE = 16;

    protected final UIColor pendingColor = UIColor.of(173, 116, 40, 255);

    public ItemNode() {
        this.backgroundColor = UIColor.of(183, 126, 50, 255);
        this.localRect.setWH(CELL_SIZE, CELL_SIZE);
        // 用于测试的
        this.currentItem = testItem;
    }

    @Override
    protected void onMouseOver(UIEventContext<MouseOver> context) {
        context.manager.getMenuContent().setHoveringItem(this.currentItem);
    }

    @Override
    protected void onMouseLeave(UIEventContext<MouseLeave> context) {
        context.manager.getMenuContent().setHoveringItem(null);
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
        final float offset = 4.0f;
        context.fill(
                worldRect.x - offset, worldRect.y - offset,
                worldRect.x + worldRect.w + offset,
                worldRect.y + worldRect.h + offset,
                this.pendingColor.color
        );
        context.renderItem(this.currentItem, (int) this.worldRect.x, (int) this.worldRect.y);
    }
}
