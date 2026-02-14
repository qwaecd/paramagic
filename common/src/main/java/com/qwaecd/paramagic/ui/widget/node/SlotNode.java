package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.inventory.UISlot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotNode extends ItemNode {
    @Nonnull
    private final UISlot slot;

    public SlotNode(@Nonnull UISlot slot) {
        this.slot = slot;
    }

    @Nonnull
    public ItemStack getExistItem() {
        return this.slot.getItem();
    }

    @Nonnull
    public UISlot getSlot() {
        return this.slot;
    }

    @Override
    @Nonnull
    public ItemStack getRenderingItem() {
        return this.slot.getItem();
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        super.render(context);
    }
}
