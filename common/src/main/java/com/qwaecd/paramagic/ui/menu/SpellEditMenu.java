package com.qwaecd.paramagic.ui.menu;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.inventory.SetOperatorAction;
import com.qwaecd.paramagic.network.packet.inventory.S2CSubmitEditedParaDataResultPacket;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorMap;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.spelltree.ParaSpellTreeData;
import com.qwaecd.paramagic.thaumaturgy.spelltree.PlayerOffhandSpellTreeEditTarget;
import com.qwaecd.paramagic.thaumaturgy.spelltree.SpellNodeData;
import com.qwaecd.paramagic.thaumaturgy.spelltree.SpellTreeDeletionHandler;
import com.qwaecd.paramagic.thaumaturgy.spelltree.SpellTreeEditTarget;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import com.qwaecd.paramagic.ui.inventory.ContainerHolder;
import com.qwaecd.paramagic.ui.inventory.PlayerInventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.SlotActionHandler;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import com.qwaecd.paramagic.ui_project.edit_table.RemovedOperatorHandler;
import com.qwaecd.paramagic.world.item.ParaOperatorItem;
import com.qwaecd.paramagic.world.item.content.ParaCrystalItem;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpellEditMenu extends AbstractContainerMenu implements SlotActionHandler, SpellTreeEditTarget {
    private final ContainerLevelAccess access;
    @Getter
    private final PlayerInventoryHolder playerInventory;
    private final ContainerHolder container;
    @Nonnull
    private final SpellTreeEditTarget editTarget;
    private final SpellTreeDeletionHandler deletionHandler = SpellTreeDeletionHandler.NO_OP;

    public SpellEditMenu(int containerId, Inventory inv) {
        this(containerId, inv, PlayerOffhandSpellTreeEditTarget.INSTANCE);
    }

    public SpellEditMenu(int containerId, Inventory inv, @Nonnull SpellTreeEditTarget editTarget) {
        this(containerId, inv, ContainerLevelAccess.NULL, new SimpleContainer(1), editTarget);
    }

    public SpellEditMenu(int containerId, Inventory inv, ContainerLevelAccess access, Container container) {
        this(containerId, inv, access, container, new SpellTreeEditTarget() {
            @Override
            @Nonnull
            public ItemStack getCrystalStack(@Nonnull ServerPlayer player) {
                return container.getItem(0);
            }

            @Override
            public void markChanged(@Nonnull ServerPlayer player) {
                container.setChanged();
            }
        });
    }

    private SpellEditMenu(
            int containerId,
            Inventory inv,
            ContainerLevelAccess access,
            Container container,
            @Nonnull SpellTreeEditTarget editTarget
    ) {
        super(ModMenuTypes.SPELL_EDIT_TABLE_MENU_TYPE, containerId);
        this.access = access;
        this.playerInventory = new PlayerInventoryHolder(inv);
        this.editTarget = editTarget;

        container.startOpen(inv.player);
        this.container = new ContainerHolder(container);
        for (int i = 0; i < 4 * 9; i++) {
            this.addSlot(new UISlot(this.playerInventory, i));
        }
        this.addSlot(new UISlot(this.playerInventory, Inventory.SLOT_OFFHAND, 4 * 9));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    public ContainerHolder getContainer() {
        return this.container;
    }

    @Override
    @Nonnull
    public ItemStack getCrystalStack(@Nonnull ServerPlayer player) {
        return this.editTarget.getCrystalStack(player);
    }

    @Override
    public void markChanged(@Nonnull ServerPlayer player) {
        this.editTarget.markChanged(player);
        this.broadcastChanges();
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
        ItemStack crystal = this.getCrystalStack(player);

        boolean success;
        if (CrystalComponentUtils.containsParaOperatorInPath(nodePath, crystal)) {
            success = this.targetContainsItem(nodePath, crystal, carried);
        } else {
            success = this.targetHasNoItem(nodePath, crystal, carried);
        }

        if (success) {
            this.markChanged(player);
        }
    }

    @Override
    public void submitEditedParaData(ServerPlayer player, ParaData paraData) {
        this.submitEditedParaData(player, paraData, -1L, -1);
    }

    public void submitEditedParaData(ServerPlayer player, ParaData paraData, long cacheToken, int cacheVersion) {
        ItemStack crystal = this.getCrystalStack(player);
        if (!(crystal.getItem() instanceof ParaCrystalItem)) {
            this.sendSubmitResult(player, false, cacheToken, cacheVersion);
            return;
        }
        if (!this.isAcceptableParaData(paraData)) {
            Paramagic.LOG.warn("Rejected edited ParaData from player {} because validation failed.", player.getName().getString());
            this.sendSubmitResult(player, false, cacheToken, cacheVersion);
            return;
        }

        ParaCrystalData crystalData = CrystalComponentUtils.getComponentFromItemStack(crystal);
        List<OperatorMap.Entry> oldOperators = crystalData != null
                ? crystalData.getOperatorEntriesSnapshot()
                : List.of();
        this.access.execute((level, blockPos) -> RemovedOperatorHandler.handleRemovedOperators(level, blockPos, oldOperators));

        if (crystalData == null) {
            crystalData = new ParaCrystalData(paraData);
        } else {
            crystalData.clearOperators();
            crystalData.setParaData(paraData);
        }

        CrystalComponentUtils.writeComponentToItemStack(crystal, crystalData);
        this.markChanged(player);
        this.sendSubmitResult(player, true, cacheToken, cacheVersion);
    }

    public boolean addSpellTreeNode(
            @Nonnull ServerPlayer player,
            int version,
            @Nonnull String parentNodeId,
            int childIndex,
            boolean useCarriedOperator
    ) {
        ParaOpId carriedOperatorId = null;
        if (useCarriedOperator) {
            carriedOperatorId = this.getCarriedOperatorId();
            if (carriedOperatorId == null) {
                return false;
            }
        }

        ParaCrystalData crystalData = this.getOrCreateCrystalData(player);
        if (crystalData == null || !this.isExpectedVersion(crystalData, version)) {
            return false;
        }

        try {
            crystalData.getSpellTreeData().addNode(parentNodeId, childIndex, carriedOperatorId);
        } catch (IllegalArgumentException e) {
            Paramagic.LOG.warn("Rejected AddSpellTreeNode from player {}: {}", player.getName().getString(), e.getMessage());
            return false;
        }

        if (useCarriedOperator) {
            this.getCarried().shrink(1);
        }
        return this.writeCrystalData(player, crystalData);
    }

    public boolean deleteSpellTreeSubtree(@Nonnull ServerPlayer player, int version, @Nonnull String nodeId) {
        ParaCrystalData crystalData = this.getOrCreateCrystalData(player);
        if (crystalData == null || !this.isExpectedVersion(crystalData, version)) {
            return false;
        }

        ParaSpellTreeData treeData = crystalData.getSpellTreeData();
        if (treeData.getRoot().getNodeId().equals(nodeId)) {
            return false;
        }
        List<SpellNodeData> removedNodes;
        try {
            removedNodes = treeData.collectSubtree(nodeId);
        } catch (IllegalArgumentException e) {
            return false;
        }

        this.deletionHandler.onDeleteSubtree(player, player.level(), removedNodes);

        List<SpellNodeData> removedDuringDelete = new ArrayList<>();
        if (!treeData.deleteSubtree(nodeId, removedDuringDelete)) {
            return false;
        }
        return this.writeCrystalData(player, crystalData);
    }

    public boolean setSpellTreeNodeOperator(
            @Nonnull ServerPlayer player,
            int version,
            @Nonnull String nodeId,
            @Nonnull SetOperatorAction action
    ) {
        ParaCrystalData crystalData = this.getOrCreateCrystalData(player);
        if (crystalData == null || !this.isExpectedVersion(crystalData, version)) {
            return false;
        }

        ParaOpId operatorId;
        if (action == SetOperatorAction.CLEAR) {
            operatorId = null;
        } else {
            operatorId = this.getCarriedOperatorId();
            if (operatorId == null) {
                return false;
            }
        }

        if (!crystalData.getSpellTreeData().setOperator(nodeId, operatorId)) {
            return false;
        }
        if (action == SetOperatorAction.FROM_CARRIED) {
            this.getCarried().shrink(1);
        }
        return this.writeCrystalData(player, crystalData);
    }

    @Nullable
    private ParaCrystalData getOrCreateCrystalData(@Nonnull ServerPlayer player) {
        ItemStack crystal = this.getCrystalStack(player);
        if (!(crystal.getItem() instanceof ParaCrystalItem)) {
            return null;
        }
        ParaCrystalData crystalData = CrystalComponentUtils.getComponentFromItemStack(crystal);
        return crystalData == null ? new ParaCrystalData() : crystalData;
    }

    private boolean writeCrystalData(@Nonnull ServerPlayer player, @Nonnull ParaCrystalData crystalData) {
        ItemStack crystal = this.getCrystalStack(player);
        if (!(crystal.getItem() instanceof ParaCrystalItem)) {
            return false;
        }
        CrystalComponentUtils.writeComponentToItemStack(crystal, crystalData);
        this.markChanged(player);
        return true;
    }

    private boolean isExpectedVersion(@Nonnull ParaCrystalData crystalData, int version) {
        return version == crystalData.getSpellTreeData().getVersion();
    }

    @Nullable
    private ParaOpId getCarriedOperatorId() {
        ItemStack carried = this.getCarried();
        if (!(carried.getItem() instanceof ParaOperatorItem operatorItem)) {
            return null;
        }
        return operatorItem.getOperatorId();
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

    private boolean isAcceptableParaData(@Nonnull ParaData paraData) {
        try {
            return this.isAcceptableComponent(paraData.rootComponent, 1);
        } catch (RuntimeException e) {
            Paramagic.LOG.warn("Failed to validate edited ParaData on server.", e);
            return false;
        }
    }

    private boolean isAcceptableComponent(@Nonnull ParaComponentData componentData, int depth) {
        if (depth > ParaTree.recursionLimit) {
            return false;
        }
        for (ParaComponentData child : componentData.getChildren()) {
            if (!this.isAcceptableComponent(child, depth + 1)) {
                return false;
            }
        }
        return true;
    }

    private void sendSubmitResult(ServerPlayer player, boolean success, long cacheToken, int cacheVersion) {
        if (cacheToken < 0L || cacheVersion < 0) {
            return;
        }
        Networking.get().sendToPlayer(player, new S2CSubmitEditedParaDataResultPacket(success, cacheToken, cacheVersion));
    }
}
