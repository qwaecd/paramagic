package com.qwaecd.paramagic.ui_project.wand.content.inventory;

import com.qwaecd.paramagic.ui.MenuContent;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UIKeyListener;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 将 InventoryUI 的限定输入集合翻译为原版容器 ClickType。
 * 库存改动始终由 AbstractContainerMenu 和服务端处理。
 */
final class InventoryInteractionController implements UIKeyListener {
    private final InventoryUI owner;
    private final InventoryHolder inventory;
    private final Map<Integer, UISlot> slotsById = new LinkedHashMap<>();
    private final Set<Slot> quickCraftSlots = new LinkedHashSet<>();

    @Nullable
    private UISlot lastClickedSlot;
    private boolean quickCrafting;
    private int quickCraftButton;
    private int quickCraftType;

    InventoryInteractionController(@Nonnull InventoryUI owner, @Nonnull InventoryHolder inventory) {
        this.owner = owner;
        this.inventory = inventory;
    }

    void attach(@Nonnull UIManager manager) {
        this.slotsById.clear();
        for (var slot : manager.getMenuContentOrThrow().getMenu().slots) {
            if (slot instanceof UISlot uiSlot && uiSlot.getInventoryHolder() == this.inventory) {
                this.slotsById.put(uiSlot.getSlotId(), uiSlot);
            }
        }
        manager.registerKeyListener(this);
    }

    void detach(@Nonnull UIManager manager) {
        this.cancelQuickCraft(manager);
        manager.unregisterKeyListener(this);
        this.slotsById.clear();
    }

    void onMouseClick(@Nonnull UIEventContext<MouseClick> context) {
        UISlot slot = this.getTargetSlot(context.getTargetNode());
        if (slot == null) {
            return;
        }
        this.handleClick(context.manager, slot, context.event.button);
        context.consumeAndStopPropagation();
    }

    void onDoubleClick(@Nonnull UIEventContext<DoubleClick> context) {
        UISlot slot = this.getTargetSlot(context.getTargetNode());
        if (slot == null) {
            return;
        }

        if (context.event.button == 0
                && slot == this.lastClickedSlot
                && context.manager.getMenuContentOrThrow().getMenu().canTakeItemForPickAll(ItemStack.EMPTY, slot)) {
            this.click(context.manager, slot, 0, ClickType.PICKUP_ALL);
        } else {
            // UIManager 已把本次点击识别为双击；不在允许的左键同槽双击条件下时，仍保持普通点击语义。
            this.handleClick(context.manager, slot, context.event.button);
        }
        this.lastClickedSlot = slot;
        context.consumeAndStopPropagation();
    }

    boolean onMouseRelease(@Nonnull UIEventContext<MouseRelease> context) {
        if (!this.quickCrafting || context.event.button != this.quickCraftButton) {
            return false;
        }

        try {
            if (!this.quickCraftSlots.isEmpty()) {
                this.click(context.manager, null, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftType), ClickType.QUICK_CRAFT);
                for (Slot quickCraftSlot : this.quickCraftSlots) {
                    if (quickCraftSlot instanceof UISlot slot) {
                        this.click(context.manager, slot, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftType), ClickType.QUICK_CRAFT);
                    }
                }
                this.click(context.manager, null, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftType), ClickType.QUICK_CRAFT);
            } else {
                // 原版在没有收集到可拖拽槽位时，退回为 release 位置上的普通点击。
                UISlot releaseSlot = this.getTargetSlot(
                        this.owner.getTopmostHitNode((float) context.event.mouseX, (float) context.event.mouseY)
                );
                if (releaseSlot != null) {
                    this.click(context.manager, releaseSlot, this.quickCraftButton, ClickType.PICKUP);
                }
            }
        } finally {
            this.cancelQuickCraft(context.manager);
        }
        return true;
    }

    void consumeMouseRelease(@Nonnull UIEventContext<MouseRelease> context) {
        if (this.getTargetSlot(context.getTargetNode()) != null) {
            context.consume();
        }
    }

    void onMouseMove(double mouseX, double mouseY) {
        if (!this.quickCrafting) {
            return;
        }
        UINode target = this.owner.getTopmostHitNode((float) mouseX, (float) mouseY);
        UISlot slot = this.getTargetSlot(target);
        if (slot != null) {
            this.addQuickCraftSlot(slot);
        }
    }

    /**
     * 返回原版 quick-craft 在当前帧会显示于该槽位的预测物品；不会修改真实库存。
     */
    @Nullable
    ItemStack getQuickCraftPreview(int slotId) {
        if (!this.quickCrafting) {
            return null;
        }
        UISlot slot = this.slotsById.get(slotId);
        if (slot == null || !this.quickCraftSlots.contains(slot)) {
            return null;
        }

        MenuContent content = this.owner.getManager().getMenuContentOrThrow();
        ItemStack carried = content.getCarried();
        if (carried.isEmpty()) {
            return null;
        }

        ItemStack existing = slot.getItem();
        int existingCount = existing.isEmpty() ? 0 : existing.getCount();
        int maximum = Math.min(carried.getMaxStackSize(), slot.getMaxStackSize(carried));
        int previewCount = AbstractContainerMenu.getQuickCraftPlaceCount(
                this.quickCraftSlots, this.quickCraftType, carried
        ) + existingCount;
        return carried.copyWithCount(Math.min(previewCount, maximum));
    }

    @Override
    public boolean onKeyPressed(UIManager manager, int keyCode, int scanCode, int modifiers) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.keySwapOffhand.matches(keyCode, scanCode)) {
            // 本界面的契约禁止移动副手物品。
            return true;
        }

        UISlot slot = this.getTargetSlot(manager.getMouseOverNode());
        if (slot == null) {
            return false;
        }

        if (minecraft.options.keyDrop.matches(keyCode, scanCode)) {
            if (slot.hasItem()) {
                this.click(manager, slot, UIManager.hasCtrlKeyDown() ? 1 : 0, ClickType.THROW);
            }
            return true;
        }

        if (manager.getMenuContentOrThrow().getCarried().isEmpty()) {
            for (int i = 0; i < 9; i++) {
                if (minecraft.options.keyHotbarSlots[i].matches(keyCode, scanCode)) {
                    this.click(manager, slot, i, ClickType.SWAP);
                    return true;
                }
            }
        }
        return false;
    }

    private void handleClick(@Nonnull UIManager manager, @Nonnull UISlot slot, int button) {
        MenuContent content = manager.getMenuContentOrThrow();
        ItemStack carried = content.getCarried();
        if (button == 0 || button == 1) {
            if (carried.isEmpty()) {
                this.click(manager, slot, button, ClickType.PICKUP);
                this.lastClickedSlot = slot;
            } else {
                this.beginQuickCraft(manager, slot, button);
            }
            return;
        }

        if (button == 2 && carried.isEmpty() && slot.hasItem() && this.isCreativeMode()) {
            this.click(manager, slot, button, ClickType.CLONE);
        }
    }

    private void beginQuickCraft(@Nonnull UIManager manager, @Nonnull UISlot initialSlot, int button) {
        this.quickCrafting = true;
        this.quickCraftButton = button;
        this.quickCraftType = button == 0 ? 0 : 1;
        this.quickCraftSlots.clear();
        this.addQuickCraftSlot(initialSlot);
        manager.captureNode(this.owner);
    }

    private void addQuickCraftSlot(@Nonnull UISlot slot) {
        if (this.quickCraftSlots.contains(slot)) {
            return;
        }
        MenuContent content = this.owner.getManager().getMenuContentOrThrow();
        ItemStack carried = content.getCarried();
        AbstractContainerMenu menu = content.getMenu();
        if (carried.isEmpty()
                || carried.getCount() <= this.quickCraftSlots.size()
                || !AbstractContainerMenu.canItemQuickReplace(slot, carried, true)
                || !slot.mayPlace(carried)
                || !menu.canDragTo(slot)) {
            return;
        }
        this.quickCraftSlots.add(slot);
    }

    private void cancelQuickCraft(@Nonnull UIManager manager) {
        boolean wasQuickCrafting = this.quickCrafting;
        this.quickCrafting = false;
        this.quickCraftSlots.clear();
        if (wasQuickCrafting) {
            manager.releaseCapture();
        }
    }

    private void click(@Nonnull UIManager manager, @Nullable UISlot slot, int button, @Nonnull ClickType type) {
        manager.getMenuContentOrThrow().getScreen().clickMenuSlot(slot, button, type);
    }

    private boolean isCreativeMode() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.gameMode != null && minecraft.gameMode.hasInfiniteItems();
    }

    @Nullable
    private UISlot getTargetSlot(@Nullable UINode node) {
        if (!(node instanceof InventoryItemNode itemNode) || !this.owner.containsInSubtree(node)) {
            return null;
        }
        return this.slotsById.get(itemNode.getSlotId());
    }
}
