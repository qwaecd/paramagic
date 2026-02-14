package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.inventory.UISlot;
import com.qwaecd.paramagic.ui.util.UIColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public void setRenderingItem(@Nullable ItemStack itemStack) {
        this.slot.set(itemStack == null ? ItemStack.EMPTY : itemStack);
        super.setRenderingItem(itemStack);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        super.render(context);
    }

    @Override
    public void renderDebug(@Nonnull UIRenderContext context) {
        super.renderDebug(context);
        if (this.getRenderingItem().isEmpty()) {
            context.drawText(
                    "null",
                    worldRect.x,
                    worldRect.y + 4.0f,
                    UIColor.WHITE
            );
        }
    }
}
