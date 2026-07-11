package com.qwaecd.paramagic.ui_project.wand.content.inventory;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.MenuContent;
import com.qwaecd.paramagic.ui.api.TooltipContent;
import com.qwaecd.paramagic.ui.api.TooltipQuery;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.*;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public final class InventoryItemNode extends UINode {
    private final InventoryHolder playerInv;
    private final int slot;

    public static final int SLOT_SIZE = 16;
    private static final float SLOT_BACKGROUND_OFFSET_X = -3.0f;
    private static final float SLOT_BACKGROUND_OFFSET_Y = -3.0f;
    private static final int highLightColor = -2130706433;

    private boolean isHovering = false;
    private float renderAlpha = 0.0f;

    public InventoryItemNode(int slot, InventoryHolder playerInv) {
        super();
        this.slot = slot;
        this.playerInv = playerInv;
        this.layoutRect.setWH(SLOT_SIZE, SLOT_SIZE);
    }

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        return constraints.constrain(MeasureResult.of(SLOT_SIZE, SLOT_SIZE));
    }

    @Override
    protected void onMouseOver(UIEventContext<MouseOver> context) {
        this.isHovering = true;
    }

    @Override
    protected void onMouseLeave(UIEventContext<MouseLeave> context) {
        this.isHovering = false;
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        MenuContent menuContent = context.manager.getMenuContentOrThrow();
        for (var menuSlot : menuContent.getMenu().slots) {
            if (menuSlot instanceof UISlot uiSlot && uiSlot.getSlotId() == this.slot) {
                menuContent.getScreen().slotClicked(uiSlot, context.event.button, ClickType.PICKUP);
                context.consume();
                return;
            }
        }
    }

    @Override
    protected void onDoubleClick(UIEventContext<DoubleClick> context) {
        MenuContent menuContent = context.manager.getMenuContentOrThrow();
        for (var menuSlot : menuContent.getMenu().slots) {
            if (menuSlot instanceof UISlot uiSlot && uiSlot.getSlotId() == this.slot) {
                menuContent.getScreen().slotClicked(uiSlot, context.event.button, ClickType.PICKUP_ALL);
                context.consume();
                return;
            }
        }
    }

    @Override
    protected void onMouseRelease(UIEventContext<MouseRelease> context) {
        context.consume();
    }

    @Override
    protected void onAttached(@NotNull UIManager manager) {
        this.animateFloat(
                this.renderAlpha,
                1.0f,
                0.6f + this.slot * 0.02f,
                EasingFunction.smoothstep,
                Interpolation::linear,
                (v) -> this.renderAlpha = v <= 0.3f ? 0.0f : v
        ).setDelay(0.2f);
    }

    @Override
    protected void render(@NotNull UIRenderContext context) {
        super.render(context);
        if (this.renderAlpha <= 0.0f) {
            return;
        }
        context.renderSpriteWithAlpha(
                WEAssets.ITEM_RECT,
                Math.round(this.finalRect.x + SLOT_BACKGROUND_OFFSET_X),
                Math.round(this.finalRect.y + SLOT_BACKGROUND_OFFSET_Y),
                this.renderAlpha
        );

        ItemStack itemStack = this.getRenderingItem();
        context.renderItem(itemStack, Math.round(this.finalRect.x), Math.round(this.finalRect.y));
        context.renderItemDecorations(itemStack, Math.round(this.finalRect.x), Math.round(this.finalRect.y));
        if (this.isHovering) {
            this.renderSlotHighlight(context);
        }
    }

    @Nonnull
    private ItemStack getRenderingItem() {
        return this.playerInv.getStackInSlot(this.slot);
    }

    @Override
    public TooltipContent getTooltip(@Nonnull TooltipQuery query) {
        return UINode.getTooltipFromItem(this.getRenderingItem());
    }

    private void renderSlotHighlight(UIRenderContext context) {
        context.fillRect(
                Math.round(this.finalRect.x), Math.round(this.finalRect.y),
                Math.round(this.finalRect.w), Math.round(this.finalRect.h),
                highLightColor
        );
    }
}
