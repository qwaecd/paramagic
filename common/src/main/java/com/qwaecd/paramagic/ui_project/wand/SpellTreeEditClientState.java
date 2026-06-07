package com.qwaecd.paramagic.ui_project.wand;

import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.thaumaturgy.spelltree.ParaSpellTreeData;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.world.item.content.ParaCrystalItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public final class SpellTreeEditClientState {
    private static final int TARGET_SLOT = Inventory.SLOT_OFFHAND;

    @Nonnull
    private final InventoryHolder playerInventory;
    private int editEpoch;
    private int nextSeq = 0;
    @Nonnull
    private ParaSpellTreeData treeData = ParaSpellTreeData.empty();

    public SpellTreeEditClientState(@Nonnull InventoryHolder playerInventory, int editEpoch) {
        this.playerInventory = playerInventory;
        this.editEpoch = editEpoch;
        this.rebuildFromTargetStack();
    }

    public int getEditEpoch() {
        return this.editEpoch;
    }

    public int getTargetSlot() {
        return TARGET_SLOT;
    }

    public int getBaseVersion() {
        return this.treeData.getVersion();
    }

    public int nextSeq() {
        return this.nextSeq++;
    }

    @Nonnull
    public String peekExpectedNodeId() {
        return this.treeData.peekNextNodeId();
    }

    @Nonnull
    public ParaSpellTreeData getTreeData() {
        return this.treeData;
    }

    public boolean acceptRejectedEdit(int editEpoch) {
        if (editEpoch < this.editEpoch) {
            return false;
        }
        this.editEpoch = editEpoch;
        this.rebuildFromTargetStack();
        return true;
    }

    public void rebuildFromTargetStack() {
        ItemStack targetStack = this.playerInventory.getStackInSlot(this.getTargetSlot());
        if (!(targetStack.getItem() instanceof ParaCrystalItem)) {
            this.treeData = ParaSpellTreeData.empty();
            return;
        }

        ParaCrystalData crystalData = CrystalComponentUtils.getComponentFromItemStack(targetStack);
        this.treeData = crystalData == null ? ParaSpellTreeData.empty() : crystalData.getSpellTreeData();
    }
}
