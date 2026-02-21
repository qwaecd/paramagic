package com.qwaecd.paramagic.ui.menu;

import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import com.qwaecd.paramagic.ui.inventory.ContainerHolder;
import com.qwaecd.paramagic.ui.inventory.PlayerInventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.SlotActionHandler;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import com.qwaecd.paramagic.world.item.ParaOperatorItem;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
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
    public boolean stillValid(Player player) {
        return this.access.evaluate((level, blockPos) -> player.distanceToSqr((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    public void clickNode(ServerPlayer player, String nodePath) {
        ItemStack carried = this.getCarried();
        ItemStack crystal = this.container.getStackInSlot(0);

        boolean success;
        if (CrystalComponentUtils.containsParaOperatorInPath(nodePath, crystal)) {
            success = this.targetContainsItem(nodePath, crystal, carried);
        } else {
            success = this.targetHasNoItem(nodePath, crystal, carried);
        }

        if (success) {
            this.container.getContainer().setChanged();
        }
    }

    private boolean targetHasNoItem(String nodePath, ItemStack crystal, ItemStack carried) {
        Item carriedItem = carried.getItem();
        if (!(carriedItem instanceof ParaOperatorItem operatorItem)) {
            // 手上物品为空 || 物品不是操作符
            // 无法进行任何操作
            return false;
        }
        ParaOpId operatorId = operatorItem.getOperatorId();
        boolean success = CrystalComponentUtils.insertParaOperatorFromPathInItemStack(operatorId, nodePath, crystal);
        if (success) {
            carried.shrink(1);
        }
        return success;
    }

    private boolean targetContainsItem(String nodePath, ItemStack crystal, ItemStack carried) {
        if (carried.isEmpty()) {
            // 目标物品存在 手上物品为空
            // 将目标物品提取到手上
            ItemStack removed = CrystalComponentUtils.removeParaOperatorFromPathInItemStack(nodePath, crystal);
            this.setCarried(removed);
            return true;
        }
        Item carriedItem = carried.getItem();

        // 目标物品存在 手上物品不空
        if (!(carriedItem instanceof ParaOperatorItem)) {
            // 手上物品无法进行任何操作
            return false;
        }
        // 手上物品是操作符物品
        ItemStack toRemoved = CrystalComponentUtils.noRemoveGetParaOperatorFromPathInItemStack(nodePath, crystal);
        if (ItemStack.isSameItemSameTags(toRemoved, carried)) {
            // 进行堆叠尝试
            while (!toRemoved.isEmpty() && carried.getCount() < carried.getMaxStackSize()) {
                toRemoved.shrink(1);
                carried.grow(1);
            }
            // 成功将物品转移到手上则 true，否则是 false
            if (toRemoved.isEmpty()) {
                CrystalComponentUtils.removeParaOperatorFromPathInItemStack(nodePath, crystal);
                return true;
            }
            return false;
        }
        // 目标物品存在 手上操作符物品不空 二者无法堆叠
        if (carried.getCount() > 1) {
            return false;
        }
        CrystalComponentUtils.removeParaOperatorFromPathInItemStack(nodePath, crystal);
        ParaOpId operatorId = ((ParaOperatorItem) carried.getItem()).getOperatorId();
        boolean success = CrystalComponentUtils.insertParaOperatorFromPathInItemStack(operatorId, nodePath, crystal);
        if (success) {
            this.setCarried(toRemoved);
        }
        return success;
    }
}
