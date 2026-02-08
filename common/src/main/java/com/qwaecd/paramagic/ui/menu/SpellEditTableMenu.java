package com.qwaecd.paramagic.ui.menu;

import com.qwaecd.paramagic.ui.PlayerInventoryHolder;
import com.qwaecd.paramagic.ui.inventory.FakeSlot;
import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SpellEditTableMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    @Getter
    private final PlayerInventoryHolder playerInventory;

    public SpellEditTableMenu(int containerId, Inventory inv) {
        this(containerId, inv, ContainerLevelAccess.NULL);
    }

    /**
     * 爱来自 Claude Opus 4.6
     * <pre>
     * ┌─────────────────────────── GUI ───────────────────────────┐
     * │ [ 9][10][11]...[17]  ← 玩家背包第0行 (103-18=85)            │
     * │ [18][19][20]...[26]  ← 玩家背包第1行                        │
     * │ [27][28][29]...[35]  ← 玩家背包第2行                        │
     * │                                                           │
     * │ [ 0][ 1][ 2]...[ 8]  ← 快捷栏 (161-18=143)                 │
     * └───────────────────────────────────────────────────────────┘
     * </pre>
     */
    public SpellEditTableMenu(int containerId, Inventory inv, ContainerLevelAccess access) {
        super(ModMenuTypes.SPELL_EDIT_TABLE_MENU_TYPE, containerId);
        this.access = access;
        this.playerInventory = new PlayerInventoryHolder(inv);

        for(int l = 0; l < 3; ++l) {
            for(int i = 0; i < 9; ++i) {
                this.addSlot(new FakeSlot(this.playerInventory, i + l * 9 + 9));
            }
        }

        for(int i = 0; i < 9; ++i) {
            this.addSlot(new FakeSlot(this.playerInventory, i));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.access.evaluate((level, blockPos) -> player.distanceToSqr((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D) <= 64.0D, true);
    }
}
