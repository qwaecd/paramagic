package com.qwaecd.paramagic.ui_project.wand;

import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui_project.wand.content.head.HeaderUI;
import com.qwaecd.paramagic.ui_project.wand.content.inventory.InventoryUI;
import com.qwaecd.paramagic.ui_project.wand.content.tree.TreeContent;


public final class WandEditUI extends UINode {
    private final HeaderUI headerUI;
    private final InventoryUI inventoryUI;
    private final TreeContent treeContent;

    public WandEditUI() {
        super();
        this.headerUI = new HeaderUI();
        this.inventoryUI = new InventoryUI();
        this.treeContent = new TreeContent();

        this.addChild(this.headerUI);
        this.addChild(this.inventoryUI);
        this.addChild(this.treeContent);
    }

    @Override
    protected void arrangeChildren() {
        final float w = this.layoutRect.w;
        final float h = this.layoutRect.h;
        // from HeaderUI
        final float xPercent = 0.8f;
        final float yPercent = 0.1f;
        float x = w * (1.0f - xPercent) / 2.0f;
        float y = h * yPercent;

        Rect headerRect = this.headerUI.getLayoutRect();
        headerRect.setXY(x, y);
        headerRect.setWH(this.headerUI.getMeasuredWidth(), this.headerUI.getMeasuredHeight());

        Rect invRect = this.inventoryUI.getLayoutRect();
        invRect.setXY(x, y + 16.0f);
        invRect.setWH(this.inventoryUI.getMeasuredWidth(), this.inventoryUI.getMeasuredHeight());

        Rect treeRect = this.treeContent.getLayoutRect();
        treeRect.setXY(w - this.treeContent.getMeasuredWidth() - x, y + 16.0f);
        treeRect.setWH(this.treeContent.getMeasuredWidth(), this.treeContent.getMeasuredHeight());
        for (UINode child : this.children) {
            child.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
        }
    }
}
