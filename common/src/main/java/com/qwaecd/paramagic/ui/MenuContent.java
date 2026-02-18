package com.qwaecd.paramagic.ui;

import com.qwaecd.paramagic.ui.inventory.IContainerScreen;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.inventory.PlayerInventoryHolder;
import com.qwaecd.paramagic.ui.widget.node.ItemNode;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MenuContent {
    private final AbstractContainerMenu menu;
    private final IContainerScreen screen;
    private final PlayerInventoryHolder playerInventory;
    @Nullable
    private ItemNode hoveringItemNode = null;

    public MenuContent(AbstractContainerMenu menu, IContainerScreen screen, Inventory playerInventory) {
        this.menu = menu;
        this.screen = screen;
        this.playerInventory = new PlayerInventoryHolder(playerInventory);
    }

    public AbstractContainerMenu getMenu() {
        return this.menu;
    }

    @Nullable
    public ItemNode getHoveringItemNode() {
        return hoveringItemNode;
    }

    public void setHoveringItemNode(@Nullable ItemNode hoveringItemNode) {
        this.hoveringItemNode = hoveringItemNode;
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
