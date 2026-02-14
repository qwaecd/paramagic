package com.qwaecd.paramagic.ui;

import com.qwaecd.paramagic.ui.inventory.IContainerScreen;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MenuContent {
    private final AbstractContainerMenu menu;
    private final IContainerScreen screen;
    private final PlayerInventoryHolder playerInventory;
    @Nonnull
    private ItemStack hoveringItem = ItemStack.EMPTY;

    public MenuContent(AbstractContainerMenu menu, IContainerScreen screen, Inventory playerInventory) {
        this.menu = menu;
        this.screen = screen;
        this.playerInventory = new PlayerInventoryHolder(playerInventory);
    }

    @Nonnull
    public ItemStack getHoveringItem() {
        return hoveringItem;
    }

    public void setHoveringItem(@Nullable ItemStack hoveringItem) {
        this.hoveringItem = hoveringItem == null ? ItemStack.EMPTY : hoveringItem;
    }

    @Nonnull
    public InventoryHolder getPlayerInventory() {
        return this.playerInventory;
    }

    @Nonnull
    public IContainerScreen getScreen() {
        return this.screen;
    }

    public void setCarried(ItemStack stack) {
        this.menu.setCarried(stack);
    }

    public ItemStack getCarried() {
        return this.menu.getCarried();
    }
}
