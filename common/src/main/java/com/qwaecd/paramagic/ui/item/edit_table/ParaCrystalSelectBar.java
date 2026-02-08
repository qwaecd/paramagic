package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UITask;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.ItemNode;
import com.qwaecd.paramagic.ui.widget.UIPanel;
import com.qwaecd.paramagic.ui.widget.UIScrollView;

import java.util.ArrayList;
import java.util.List;

public class ParaCrystalSelectBar extends UIScrollView {
    private static final UITask reOvering = UITask.create(UIManager::flushMouseOvering);
    private InventoryHolder inventory;
    private final List<ItemNode> items = new ArrayList<>();
    private UIPanel panel;
    private final float panelOffsetY = 4.0f;

    public ParaCrystalSelectBar() {
        super(false);
        this.localRect.setWH(32, 180);
        this.layoutParams.disable();
        this.backgroundColor = UIColor.of(172, 122, 52, 255);
        this.clipMod = ClipMod.RECT;

        this.addListener();
    }

    private void addListener() {
        this.addListener(
                AllUIEvents.WHEEL,
                EventPhase.BUBBLING,
                (context) -> {
                    if (!context.isConsumed()) {
                        context.manager.offerDeferredTask(reOvering);
                        this.onMouseScroll(context);
                        context.consume();
                    }
                }
        );
    }

    public void setInventory(InventoryHolder inv) {
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
                inv.size(), 1, this.localRect.w, true, 4, 8, ItemNode.CELL_SIZE
        );
        for (int i = 0; i < inv.size(); i++) {
            ItemNode itemNode = new ItemNode();
            itemNode.putItem(inv.getStackInSlot(i));
            this.items.add(itemNode);
            this.panel.addItemNode(itemNode);
        }
        this.panel.updateContent();

        this.addChild(this.panel);
        this.recalculateContentExtent();
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        // 将自己置于屏幕右边中间
        float windowH = this.getWindowHeight() / this.getGuiScale();
        float windowW = this.getWindowWidth() / this.getGuiScale();
        this.localRect.setXY(
                windowW - this.localRect.w - 4.0f,
                (windowH - this.localRect.h) / 2.0f
        );

        // 内部面板置于中央偏下
        this.panel.localRect.setXY(
                (this.localRect.w - ItemNode.CELL_SIZE) / 2.0f,
                this.panelOffsetY
        );
        super.layout(parentX, parentY, parentW, parentH);
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
