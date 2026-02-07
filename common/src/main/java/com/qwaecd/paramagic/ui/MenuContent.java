package com.qwaecd.paramagic.ui;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MenuContent {
    @Nonnull
    private ItemStack hoveringItem;

    public MenuContent() {
        this.hoveringItem = ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack getHoveringItem() {
        return hoveringItem;
    }

    public void setHoveringItem(@Nullable ItemStack hoveringItem) {
        this.hoveringItem = hoveringItem == null ? ItemStack.EMPTY : hoveringItem;
    }
}
