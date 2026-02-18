package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.thaumaturgy.ParaCrystalComponent;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import com.qwaecd.paramagic.ui.core.ClipMod;
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
    @Nonnull
    private final TableContainerProvider table;
    @Nullable
    private PTTreeNode treeNode;

    private final CanvasNode canvas;

    public EditWindow(@Nonnull TableContainerProvider spellEditTableUI) {
        this.table = spellEditTableUI;
        this.localRect.setWH(220, 180);
        this.backgroundColor = UIColor.of(129, 64, 0, 255);
        this.layoutParams.center();
        this.clipMod = ClipMod.RECT;
        this.canvas = new CanvasNode();
        this.addChild(this.canvas);
    }

    void onContainerChanged(InventoryHolder inventory, UISlot slot) {
        ItemStack item = slot.getItem();
        if (!(item.getItem() instanceof ParaCrystalItem)) {
            if (this.treeNode != null) {
                this.canvas.removeChild(this.treeNode);
                this.canvas.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
            }
            return;
        }
        if (!this.table.get().isItemStack(item)) {
            if (this.treeNode != null) {
                this.canvas.removeChild(this.treeNode);
                this.canvas.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
            }
            return;
        }
        ParaCrystalComponent component = CrystalComponentUtils.getComponentFromItemStack(item);
        if (component == null) {
            if (this.treeNode != null) {
                this.canvas.removeChild(this.treeNode);
                this.canvas.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
            }
            return;
        }

        if (this.treeNode != null) {
            this.canvas.removeChild(this.treeNode);
        }
        ParaTree paraTree = new ParaTree(component.getParaData());
        paraTree.updateAll(component);
        this.treeNode = new PTTreeNode(paraTree);
        this.canvas.addChild(this.treeNode);
        this.canvas.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
    }
}
