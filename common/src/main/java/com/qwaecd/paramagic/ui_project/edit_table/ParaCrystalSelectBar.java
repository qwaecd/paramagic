package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.MenuContent;
import com.qwaecd.paramagic.ui.animation.fast.FloatUIAnimator;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.*;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.event.impl.WheelEvent;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import com.qwaecd.paramagic.ui.io.mouse.MouseButton;
import com.qwaecd.paramagic.ui.widget.UIPanel;
import com.qwaecd.paramagic.ui.widget.UIScrollView;
import com.qwaecd.paramagic.ui.widget.node.ItemNode;
import com.qwaecd.paramagic.ui.widget.node.SlotNode;
import net.minecraft.world.inventory.ClickType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ParaCrystalSelectBar extends UIScrollView {
    private static final float BAR_WIDTH = 32.0f;
    private static final float BAR_HEIGHT = 180.0f;
    private static final float RIGHT_MARGIN = 8.0f;

    private InventoryHolder inventory;

    // List 的索引并不代表着 inventory 的槽位索引，槽位索引由 SlotNode 内部维护
    private final List<SlotNode> items = new ArrayList<>();
    private UIPanel panel;
    private final float panelOffsetY = 4.0f;

    @Nullable
    private FloatUIAnimator scrollAnimator;

    private static final EditTableSprite paraSelectBar = new EditTableSprite(
            0, 0,
            40, 188,
            -4, -4
    );

    public ParaCrystalSelectBar() {
        super(false);
        this.setLayoutSize(BAR_WIDTH, BAR_HEIGHT);
        this.layoutParams.disable();
        this.clipMod = ClipMod.RECT;

        this.addListener();
    }

    private void addListener() {
        this.addListener(
                AllUIEvents.WHEEL,
                EventPhase.BUBBLING,
                (context) -> {
                    if (!context.isConsumed()) {
                        this.onMouseScroll(context);
                        context.consume();
                    }
                }
        );

        this.addListener(
                AllUIEvents.MOUSE_CLICK,
                EventPhase.BUBBLING,
                this::handleItemNodeClick
        );
        this.addListener(
                AllUIEvents.MOUSE_DOUBLE_CLICK,
                EventPhase.BUBBLING,
                this::handleItemNodeDoubleClick
        );
        this.addListener(
                AllUIEvents.MOUSE_RELEASE,
                EventPhase.BUBBLING,
                this::handleItemNodeRelease
        );
    }

    private void handleItemNodeClick(UIEventContext<MouseClick> context) {
        UINode targetNode = context.targetNode;
        if (!(targetNode instanceof SlotNode slotNode)) {
            return;
        }
        MenuContent menu = context.manager.getMenuContentOrThrow();
        UISlot slot = slotNode.getSlot();
        menu.getScreen().slotClicked(slot, context.event.button, ClickType.PICKUP);
        context.consume();
    }

    private void handleItemNodeRelease(UIEventContext<MouseRelease> context) {
        context.consume();
    }

    private void handleItemNodeDoubleClick(UIEventContext<DoubleClick> context) {
        UINode targetNode = context.targetNode;
        if (!(targetNode instanceof SlotNode slotNode)) {
            return;
        }
        MenuContent menu = context.manager.getMenuContentOrThrow();

        if (menu.getCarried().isEmpty()) {
            return;
        }

        if (context.event.button != MouseButton.LEFT.code) {
            this.handleItemNodeClick(UIEventContext.upcast(AllUIEvents.MOUSE_CLICK, context));
            return;
        }

        UISlot slot = slotNode.getSlot();
        if (UIManager.hasShiftKeyDown()) {
            menu.getScreen().slotClicked(slot, context.event.button, ClickType.QUICK_MOVE);
        } else {
            menu.getScreen().slotClicked(slot, context.event.button, ClickType.PICKUP_ALL);
        }
        context.consume();
    }

    @Override
    protected void onMouseScroll(UIEventContext<WheelEvent> context) {
        final float start = this.viewOffset;
        super.onMouseScroll(context);
        UIManager manager = context.manager;
        if (this.scrollAnimator != null) {
            // TODO: 可以实现动画合并
            if (!this.scrollAnimator.isFinished()) {
                return;
            }
            manager.removeAnimator(this.scrollAnimator);
        }
        this.scrollAnimator = this.animateFloat(
                start, this.viewOffset, 0.15f,
                EasingFunction.easeOutSine,
                Interpolation::linear,
                (interpolationValue -> this.viewOffset = interpolationValue)
        ).setOnUpdate(offset -> {
            manager.offerOveringTestTask();
            this.clampViewOffset();
            this.recalculateContentExtent();
            this.layoutChildren();
        });
    }

    public int initInventory(InventoryHolder inv) {
        if (this.inventory != null) {
            throw new IllegalStateException("Inventory has already been set");
        }
        this.inventory = inv;

        if (!this.items.isEmpty()) {
            throw new IllegalStateException("Items have already been initialized");
        }

        if (this.panel != null) {
            this.removeChild(this.panel);
        }

        this.panel = new UIPanel(
                inv.size(), 1, BAR_WIDTH, true, 4, 8, ItemNode.CELL_SIZE
        );
        for (int i = 0; i < 4 * 9; i++) {
            SlotNode slotNode = new SlotNode(new UISlot(inv, i));
            slotNode.setRenderingItem(inv.getStackInSlot(i));
            // List 的索引并不代表着 inventory 的槽位索引，槽位索引由 SlotNode 内部维护
            this.items.add(slotNode);
            this.panel.addItemNode(slotNode);
        }
        this.panel.updateContent();

        this.addChild(this.panel);
        this.recalculateContentExtent();
        return 4 * 9;
    }

    @Override
    @Nonnull
    protected MeasureResult measureSelf(@Nonnull LayoutConstraints constraints) {
        float windowH = UIManager.getWindowHeight() / UIManager.getGuiScale();
        float windowW = UIManager.getWindowWidth() / UIManager.getGuiScale();
        this.layoutRect.set(
                windowW - BAR_WIDTH - RIGHT_MARGIN,
                (windowH - BAR_HEIGHT) / 2.0f,
                BAR_WIDTH,
                BAR_HEIGHT
        );
        return MeasureResult.of(BAR_WIDTH, BAR_HEIGHT);
    }

    @Override
    protected void measureChildren(@Nonnull LayoutConstraints constraints) {
        if (this.panel == null) {
            this.contentExtent = 0.0f;
            return;
        }
        this.panel.getLayoutRect().setXY(
                (BAR_WIDTH - ItemNode.CELL_SIZE) / 2.0f,
                this.panelOffsetY
        );
        this.panel.measure(LayoutConstraints.loose(BAR_WIDTH, BAR_HEIGHT));
        this.recalculateContentExtent();
    }

    @Override
    protected void renderBackGround(UIRenderContext context) {
        context.renderSprite(paraSelectBar, this.worldRect.x + paraSelectBar.spriteOffsetX, this.worldRect.y + paraSelectBar.spriteOffsetY);
    }

    @Override
    protected void recalculateContentExtent() {
        float maxExtent = 0.0f;
        for (UINode child : this.items) {
            float end = this.isHorizontal
                    ? child.getLocalRect().x + child.getLocalRect().w
                    : child.getLocalRect().y + child.getLocalRect().h + this.panelOffsetY * 2.0f;
            if (end > maxExtent) {
                maxExtent = end;
            }
        }
        this.contentExtent = maxExtent;
    }
}
