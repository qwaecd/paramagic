package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;

public class ItemNode extends UINode {
    private final ItemStack testItem = new ItemStack(Items.STICK);
    public static final int CELL_SIZE = 16;
    public ItemNode() {
        this.backgroundColor = UIColor.of(183, 126, 50, 255);
        this.localRect.setWH(CELL_SIZE, CELL_SIZE);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        super.render(context);
        context.renderItem(testItem, (int) this.worldRect.x, (int) this.worldRect.y);
    }
}
