package com.qwaecd.paramagic.ui_project.wand;

import com.qwaecd.paramagic.network.packet.inventory.SpellTreeEditOperation;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.spelltree.ParaSpellTreeData;
import com.qwaecd.paramagic.thaumaturgy.spelltree.SpellNodeData;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.util.UINetwork;
import com.qwaecd.paramagic.world.item.ParaOperatorItem;
import com.qwaecd.paramagic.world.item.content.ParaCrystalItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.function.Supplier;

public final class SpellTreeEditClientState {
    private static final int TARGET_SLOT = Inventory.SLOT_OFFHAND;

    @Nonnull
    private final InventoryHolder playerInventory;
    @Nonnull
    private final Supplier<ItemStack> carriedStackSupplier;
    private int editEpoch;
    private int nextRequestId = 0;
    @Nonnull
    private ParaSpellTreeData treeData = ParaSpellTreeData.empty();

    public SpellTreeEditClientState(
            @Nonnull InventoryHolder playerInventory,
            @Nonnull Supplier<ItemStack> carriedStackSupplier,
            int editEpoch
    ) {
        this.playerInventory = playerInventory;
        this.carriedStackSupplier = carriedStackSupplier;
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

    @Nonnull
    public ParaSpellTreeData getTreeData() {
        return this.treeData;
    }

    public boolean acceptRejectedEdit(int editEpoch, @Nonnull ParaSpellTreeData treeData) {
        if (editEpoch < this.editEpoch) {
            return false;
        }
        this.editEpoch = editEpoch;
        this.treeData = treeData;
        return true;
    }

    public boolean submit(@Nonnull SpellTreeEditOperation operation, @Nonnull String nodeId, int childIndex) {
        int baseVersion = this.treeData.getVersion();
        if (!this.applyOptimistically(operation, nodeId, childIndex)) {
            return false;
        }
        UINetwork.sendSpellTreeEdit(this.editEpoch, this.nextRequestId++, baseVersion, operation, nodeId, childIndex);
        return true;
    }

    private boolean applyOptimistically(@Nonnull SpellTreeEditOperation operation, @Nonnull String nodeId, int childIndex) {
        return switch (operation) {
            case ADD_CHILD -> this.tryAddChild(nodeId, childIndex);
            case DELETE_SUBTREE -> this.tryDeleteSubtree(nodeId);
            case CLEAR_CHILDREN -> this.treeData.clearChildren(nodeId, new ArrayList<>());
            case INTERACT_NODE_OPERATOR -> this.tryInteractNodeOperator(nodeId);
        };
    }

    private boolean tryAddChild(@Nonnull String parentNodeId, int childIndex) {
        try {
            this.treeData.addNode(parentNodeId, childIndex, null);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private boolean tryDeleteSubtree(@Nonnull String nodeId) {
        if (this.treeData.getRoot().getNodeId().equals(nodeId)) {
            return false;
        }
        return this.treeData.deleteSubtree(nodeId, new ArrayList<>());
    }

    private boolean tryInteractNodeOperator(@Nonnull String nodeId) {
        SpellNodeData node = this.treeData.findNode(nodeId);
        if (node == null) {
            return false;
        }
        ItemStack carried = this.carriedStackSupplier.get();
        ParaOpId existingId = node.getOperatorId();
        if (existingId == null) {
            if (!(carried.getItem() instanceof ParaOperatorItem item)) {
                return false;
            }
            return this.treeData.setOperator(nodeId, item.getOperatorId());
        }
        if (carried.isEmpty()) {
            return this.treeData.setOperator(nodeId, null);
        }
        if (!(carried.getItem() instanceof ParaOperatorItem item)) {
            return false;
        }
        if (!item.getOperatorId().equals(existingId)) {
            // A single carried operator exchanges with a different operator already in the node.
            return carried.getCount() == 1 && this.treeData.setOperator(nodeId, item.getOperatorId());
        }
        if (carried.getCount() >= carried.getMaxStackSize()) {
            return false;
        }
        // A same-kind carried operator is always merged back into the cursor, including a
        // single-item cursor stack. Nodes never perform a single-item exchange.
        return this.treeData.setOperator(nodeId, null);
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
