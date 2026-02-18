package com.qwaecd.paramagic.ui.menu;

import com.qwaecd.paramagic.ui.inventory.*;
import com.qwaecd.paramagic.ui.inventory.slot.SlotAction;
import com.qwaecd.paramagic.ui.inventory.slot.SlotActionHandler;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SpellEditTableMenu extends AbstractContainerMenu implements SlotActionHandler {
    private final ContainerLevelAccess access;
    @Getter
    private final PlayerInventoryHolder playerInventory;
    private final ContainerHolder container;

    public SpellEditTableMenu(int containerId, Inventory inv) {
        this(containerId, inv, ContainerLevelAccess.NULL, new SimpleContainer(1));
    }

    public SpellEditTableMenu(int containerId, Inventory inv, ContainerLevelAccess access, Container container) {
        super(ModMenuTypes.SPELL_EDIT_TABLE_MENU_TYPE, containerId);
        this.access = access;
        this.playerInventory = new PlayerInventoryHolder(inv);

        container.startOpen(inv.player);
        this.container = new ContainerHolder(container);
        for (int i = 0; i < 4 * 9; i++) {
            this.addSlot(new UISlot(this.playerInventory, i));
        }
        this.addSlot(new UISlot(this.container, 0, 4 * 9));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    public ContainerHolder getContainer() {
        return this.container;
    }

    @Override
    public boolean canDragTo(Slot slot) {
        if (slot instanceof UISlot uiSlot) {
            return uiSlot.isDraggable();
        }
        return super.canDragTo(slot);
    }

    @Override
    public void handleSlotAction(ServerPlayer player, int slotIndex, SlotAction action, String extraData) {
        // 具体的节点树操作逻辑由后续功能实现
    }

    @Override
    public boolean stillValid(Player player) {
        return this.access.evaluate((level, blockPos) -> player.distanceToSqr((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D) <= 64.0D, true);
    }
}
