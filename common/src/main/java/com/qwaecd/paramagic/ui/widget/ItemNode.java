package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemNode extends UINode {
    private final ItemStack testItem = new ItemStack(ModItems.EXPLOSION_WAND);
    public static final int CELL_SIZE = 16;

    protected final UIColor pendingColor = UIColor.of(173, 116, 40, 255);

    public ItemNode() {
        this.backgroundColor = UIColor.of(183, 126, 50, 255);
        this.localRect.setWH(CELL_SIZE, CELL_SIZE);
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
        context.renderItem(testItem, (int) this.worldRect.x, (int) this.worldRect.y);
    }
}
