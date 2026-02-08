package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.widget.ItemNode;
import com.qwaecd.paramagic.ui.widget.UIPanel;

import java.util.ArrayList;
import java.util.List;

public class ParaCrystalSelectBar extends SelectBar {
    private InventoryHolder inventory;
    private final List<ItemNode> items = new ArrayList<>();
    private UIPanel panel;

    public ParaCrystalSelectBar() {
        // 右对齐稍微偏左
        this.layoutParams.set(0.99f, 0.5f, 1.0f, 0.5f);
        this.clipMod = ClipMod.RECT;
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
            this.panel.addChild(itemNode);
        }

        this.addChild(this.panel);
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        this.panel.localRect.setXY(
                (this.localRect.w - ItemNode.CELL_SIZE) / 2.0f,
                4.0f
        );
        super.layout(parentX, parentY, parentW, parentH);
    }
}
