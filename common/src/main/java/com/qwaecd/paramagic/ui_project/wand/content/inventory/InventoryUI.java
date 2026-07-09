package com.qwaecd.paramagic.ui_project.wand.content.inventory;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public final class InventoryUI extends UINode {
    private static final int ROW_COUNT = 9;
    private static final int COLUMN_COUNT = 4;
    private static final int SLOT_COUNT = ROW_COUNT * COLUMN_COUNT;

    private static final float HORIZONTAL_PADDING = 12.0f;
    private static final float VERTICAL_PADDING = 12.0f;
    private static final float SLOT_GAP = 2.0f + 3.0f;

    private static final float SLOT_PITCH = InventoryItemNode.SLOT_SIZE + SLOT_GAP;
    private static final float WIDTH = HORIZONTAL_PADDING * 2.0f
            + COLUMN_COUNT * InventoryItemNode.SLOT_SIZE
            + (COLUMN_COUNT - 1) * SLOT_GAP;
    private static final float HEIGHT = VERTICAL_PADDING * 2.0f
            + ROW_COUNT * InventoryItemNode.SLOT_SIZE
            + (ROW_COUNT - 1) * SLOT_GAP;

    private final InventoryHolder playerInv;
    private float offsetAlpha = 0.6f;
    private float renderAlpha = 0.1f;

    public InventoryUI(InventoryHolder playerInv) {
        super();
        this.playerInv = playerInv;
        this.addInventoryItems();
    }

    private void addInventoryItems() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            InventoryItemNode itemNode = new InventoryItemNode(i, this.playerInv);
            this.addChild(itemNode);
        }
    }

    @Override
    protected void onAttached(@Nonnull UIManager manager) {
        this.animateFloat(
                this.offsetAlpha,
                1.0f,
                0.4f,
                EasingFunction.easeOutSine,
                Interpolation::linear,
                (v -> this.offsetAlpha = v)
        );
        this.animateFloat(
                this.renderAlpha,
                1.0f,
                0.4f,
                EasingFunction.easeInOutQuad,
                Interpolation::linear,
                (v -> this.renderAlpha = v)
        );
    }

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        return constraints.constrain(MeasureResult.of(WIDTH, HEIGHT));
    }

    @Override
    protected void arrangeChildren() {
        for (int i = 0; i < this.children.size(); i++) {
            UINode child = this.children.get(i);
            int row = i % ROW_COUNT;
            int col = i / ROW_COUNT;
            float x = HORIZONTAL_PADDING + col * SLOT_PITCH;
            float y = VERTICAL_PADDING + row * SLOT_PITCH;
            Rect childRect = child.getLayoutRect();
            childRect.set(x, y, child.getMeasuredWidth(), child.getMeasuredHeight());
            child.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
        }
    }

    @Override
    protected void render(@NotNull UIRenderContext context) {
        float x = this.finalRect.x + this.finalRect.w / 2.0f * (1.0f - this.offsetAlpha);
        float y = this.finalRect.y + this.finalRect.h / 2.0f * (1.0f - this.offsetAlpha);
        context.renderNineSliceSpriteWithAlpha(
                WEAssets.INVENTORY_RECT,
                (int) x,
                (int) y,
                (int) (this.finalRect.w * this.offsetAlpha),
                (int) (this.finalRect.h * this.offsetAlpha),
                this.renderAlpha
        );
    }
}
