package com.qwaecd.paramagic.ui.menu;

import com.qwaecd.paramagic.ui.PlayerInventoryHolder;
import com.qwaecd.paramagic.ui.inventory.SlotAction;
import com.qwaecd.paramagic.ui.inventory.SlotActionHandler;
import com.qwaecd.paramagic.ui.inventory.UISlot;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SpellEditTableMenu extends AbstractContainerMenu implements SlotActionHandler {
    private final ContainerLevelAccess access;
    @Getter
    private final PlayerInventoryHolder playerInventory;

    public SpellEditTableMenu(int containerId, Inventory inv) {
        this(containerId, inv, ContainerLevelAccess.NULL);
    }

    public SpellEditTableMenu(int containerId, Inventory inv, ContainerLevelAccess access) {
        super(ModMenuTypes.SPELL_EDIT_TABLE_MENU_TYPE, containerId);
        this.access = access;
        this.playerInventory = new PlayerInventoryHolder(inv);

        for (int i = 0; i < 4 * 9; i++) {
            this.addSlot(new UISlot(this.playerInventory, i));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
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
