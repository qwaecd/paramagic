package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.node.CanvasNode;
import com.qwaecd.paramagic.ui.widget.node.PTTreeNode;
import com.qwaecd.paramagic.world.item.content.ParaCrystalItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EditWindow extends UINode {
    @Nullable
    private PTTreeNode treeNode;

    private final CanvasNode canvas;

    private boolean treeEditActive = false;

    public EditWindow() {
        this.localRect.setWH(220, 180);
        this.backgroundColor = UIColor.of(129, 64, 0, 255);
        this.layoutParams.center();
        this.clipMod = ClipMod.RECT;
        this.canvas = new CanvasNode();
        this.addChild(this.canvas);
    }

    void onContainerChanged(SpellEditTableUI mainUI, InventoryHolder container, UISlot slot) {
        ItemStack item = slot.getItem();
        if (!(item.getItem() instanceof ParaCrystalItem)) {
            this.removeTreeNode();
            return;
        }

        if (!mainUI.getContainerNode().isItemStack(item)) {
            this.removeTreeNode();
            return;
        }
        ParaCrystalData component = CrystalComponentUtils.getComponentFromItemStack(item);
        if (component == null) {
            this.removeTreeNode();
            return;
        }
        ParaTree paraTree = new ParaTree(component.getParaData());
        paraTree.updateAll(component);
        PTTreeNode ptTreeNode = new PTTreeNode(paraTree);
        this.updateTreeNode(ptTreeNode);

        UIManager manager = UIManager.getInstance();
        if (manager != null) {
            manager.offerOveringTestTask(true);
        }
    }

    void setTreeEditActive(boolean treeEditActive) {
        this.treeEditActive = treeEditActive;
        if (treeEditActive) {
            if (!this.canvas.containsChild(this.treeNode) && treeNode != null) {
                this.canvas.addChild(treeNode);
            }
            this.canvas.setIgnoreTransform(false);
        } else {
            if (this.canvas.containsChild(this.treeNode)) {
                this.canvas.removeChild(this.treeNode);
            }
            this.canvas.setIgnoreTransform(true);
        }
    }

    private void removeTreeNode() {
        if (this.treeNode != null) {
            this.canvas.removeChild(this.treeNode);
        }
        this.treeNode = null;
        this.canvas.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
    }

    private void updateTreeNode(@Nonnull PTTreeNode node) {
        if (this.treeNode != null) {
            this.canvas.removeChild(this.treeNode);
        }
        this.treeNode = node;
        if (!this.treeEditActive) {
            return;
        }
        this.canvas.addChild(node);
        this.canvas.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
    }
}
