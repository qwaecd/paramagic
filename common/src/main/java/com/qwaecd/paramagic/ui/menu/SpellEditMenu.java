package com.qwaecd.paramagic.ui.menu;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.inventory.C2SSpellTreeEditPacket;
import com.qwaecd.paramagic.network.packet.inventory.S2CSpellTreeEditRejectedPacket;
import com.qwaecd.paramagic.network.packet.inventory.SpellTreeEditRejectReason;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.thaumaturgy.spelltree.ParaSpellTreeData;
import com.qwaecd.paramagic.thaumaturgy.spelltree.PlayerOffhandSpellTreeEditTarget;
import com.qwaecd.paramagic.thaumaturgy.spelltree.SpellNodeData;
import com.qwaecd.paramagic.thaumaturgy.spelltree.SpellTreeEditTarget;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import com.qwaecd.paramagic.ui.inventory.ContainerHolder;
import com.qwaecd.paramagic.ui.inventory.PlayerInventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
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
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpellEditMenu extends AbstractContainerMenu implements SpellTreeEditTarget {
    private final ContainerLevelAccess access;
    @Getter
    private final PlayerInventoryHolder playerInventory;
    private final ContainerHolder container;
    @Nonnull
    private final SpellTreeEditTarget editTarget;
    private int editEpoch = 0;

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

    public int getEditEpoch() {
        return this.editEpoch;
    }

    public void acceptServerEditEpoch(int editEpoch) {
        if (editEpoch > this.editEpoch) {
            this.editEpoch = editEpoch;
        }
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

    /** Applies the current wand UI's operation model. All item information is derived on the server. */
    public void applySpellTreeEdit(@Nonnull ServerPlayer player, @Nonnull C2SSpellTreeEditPacket packet) {
        if (!this.isExpectedEpoch(player, packet.getEditEpoch())) {
            this.rejectSpellTreeEdit(player, packet.getRequestId(), SpellTreeEditRejectReason.OPERATION_REJECTED, this.getCurrentTreeData(player));
            return;
        }
        ParaCrystalData crystalData = this.getOrCreateCrystalData(player);
        if (crystalData == null) {
            this.rejectSpellTreeEdit(player, packet.getRequestId(), SpellTreeEditRejectReason.INVALID_TARGET, ParaSpellTreeData.empty());
            return;
        }
        ParaSpellTreeData treeData = crystalData.getSpellTreeData();
        if (!this.isExpectedVersion(crystalData, packet.getBaseVersion())) {
            this.rejectSpellTreeEdit(player, packet.getRequestId(), SpellTreeEditRejectReason.STALE_VERSION, treeData);
            return;
        }

        boolean success = switch (packet.getOperation()) {
            case ADD_CHILD -> this.addEmptySpellTreeNode(treeData, packet.getNodeId(), packet.getChildIndex());
            case DELETE_SUBTREE -> this.deleteSpellTreeSubtreeAndReturn(player, treeData, packet.getNodeId());
            case CLEAR_CHILDREN -> this.clearSpellTreeChildrenAndReturn(player, treeData, packet.getNodeId());
            case INTERACT_NODE_OPERATOR -> this.interactSpellTreeNodeOperator(player, treeData, packet.getNodeId());
        };
        if (!success) {
            this.rejectSpellTreeEdit(player, packet.getRequestId(), SpellTreeEditRejectReason.OPERATION_REJECTED, treeData);
            return;
        }
        if (!this.writeCrystalData(player, crystalData)) {
            this.rejectSpellTreeEdit(player, packet.getRequestId(), SpellTreeEditRejectReason.INVALID_TARGET, treeData);
        }
    }

    private boolean addEmptySpellTreeNode(@Nonnull ParaSpellTreeData treeData, @Nonnull String parentNodeId, int childIndex) {
        try {
            treeData.addNode(parentNodeId, childIndex, null);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private boolean deleteSpellTreeSubtreeAndReturn(@Nonnull ServerPlayer player, @Nonnull ParaSpellTreeData treeData, @Nonnull String nodeId) {
        if (treeData.getRoot().getNodeId().equals(nodeId)) {
            return false;
        }
        List<SpellNodeData> removed;
        try {
            removed = treeData.collectSubtree(nodeId);
        } catch (IllegalArgumentException ignored) {
            return false;
        }
        List<SpellNodeData> deleted = new ArrayList<>();
        if (!treeData.deleteSubtree(nodeId, deleted)) {
            return false;
        }
        this.returnOperatorsToPlayer(player, removed);
        return true;
    }

    private boolean clearSpellTreeChildrenAndReturn(@Nonnull ServerPlayer player, @Nonnull ParaSpellTreeData treeData, @Nonnull String nodeId) {
        List<SpellNodeData> removed = new ArrayList<>();
        if (!treeData.clearChildren(nodeId, removed)) {
            return false;
        }
        this.returnOperatorsToPlayer(player, removed);
        return true;
    }

    private boolean interactSpellTreeNodeOperator(@Nonnull ServerPlayer player, @Nonnull ParaSpellTreeData treeData, @Nonnull String nodeId) {
        SpellNodeData node = treeData.findNode(nodeId);
        if (node == null) {
            return false;
        }
        ItemStack carried = this.getCarried();
        ParaOpId existingId = node.getOperatorId();
        if (existingId == null) {
            if (!(carried.getItem() instanceof ParaOperatorItem item)) {
                return false;
            }
            carried.shrink(1);
            treeData.setOperator(nodeId, item.getOperatorId());
            return true;
        }
        ItemStack existingStack = this.createOperatorStack(existingId);
        if (existingStack.isEmpty()) {
            return false;
        }
        if (carried.isEmpty()) {
            this.setCarried(existingStack);
            treeData.setOperator(nodeId, null);
            return true;
        }
        if (!(carried.getItem() instanceof ParaOperatorItem carriedItem)) {
            return false;
        }
        if (!carriedItem.getOperatorId().equals(existingId)) {
            if (carried.getCount() != 1) {
                return false;
            }
            treeData.setOperator(nodeId, carriedItem.getOperatorId());
            this.setCarried(existingStack);
            return true;
        }
        if (carried.getCount() >= carried.getMaxStackSize()) {
            return false;
        }
        // Same-kind operators merge into the cursor at every positive count, including one.
        // The tree node is cleared; this operation is not a single-item exchange.
        carried.grow(1);
        treeData.setOperator(nodeId, null);
        return true;
    }

    private void returnOperatorsToPlayer(@Nonnull ServerPlayer player, @Nonnull List<SpellNodeData> removedNodes) {
        for (SpellNodeData node : removedNodes) {
            ParaOpId operatorId = node.getOperatorId();
            if (operatorId == null) {
                continue;
            }
            ItemStack stack = this.createOperatorStack(operatorId);
            if (stack.isEmpty()) {
                continue;
            }
            player.getInventory().add(stack);
            if (!stack.isEmpty()) {
                player.drop(stack, false);
            }
        }
    }

    @Nonnull
    private ItemStack createOperatorStack(@Nonnull ParaOpId operatorId) {
        ParaOperator operator = AllParaOperators.createOperator(operatorId);
        return operator == null ? ItemStack.EMPTY : operator.createOperatorItem();
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

    private boolean isExpectedEpoch(@Nonnull ServerPlayer player, int editEpoch) {
        if (editEpoch == this.editEpoch) {
            return true;
        }
        Paramagic.LOG.debug(
                "Dropped stale spell tree edit packet from player {}: packetEpoch={}, currentEpoch={}.",
                player.getName().getString(),
                editEpoch,
                this.editEpoch
        );
        return false;
    }

    private void rejectSpellTreeEdit(
            @Nonnull ServerPlayer player,
            int requestId,
            @Nonnull SpellTreeEditRejectReason reason,
            @Nonnull ParaSpellTreeData treeData
    ) {
        Networking.get().sendToPlayer(player, new S2CSpellTreeEditRejectedPacket(
                this.editEpoch, requestId, treeData.getVersion(), reason, treeData
        ));
    }

    @Nonnull
    private ParaSpellTreeData getCurrentTreeData(@Nonnull ServerPlayer player) {
        ParaCrystalData crystalData = this.getOrCreateCrystalData(player);
        return crystalData == null ? ParaSpellTreeData.empty() : crystalData.getSpellTreeData();
    }

}
