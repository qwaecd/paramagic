package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.inventory.UISlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class SlotNode extends ItemNode {
    @Nonnull
    private final UISlot slot;

    public SlotNode(@Nonnull UISlot slot) {
        this.slot = slot;
    }

    @Nonnull
    public UISlot getSlot() {
        return this.slot;
    }

    @Override
    public void setItem(@Nullable ItemStack itemStack) {
        ItemStack toSet = itemStack == null ? ItemStack.EMPTY : itemStack;
        this.slot.set(toSet);
        this.currentItem = toSet;
    }

    @Override
    public ItemStack takeItem() {
        ItemStack item = this.slot.getItem();
        this.slot.set(ItemStack.EMPTY);
        this.currentItem = ItemStack.EMPTY;
        return item;
    }

    @Override
    @Nonnull
    public ItemStack getItem() {
        return this.slot.getItem();
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        super.render(context);
    }
}
