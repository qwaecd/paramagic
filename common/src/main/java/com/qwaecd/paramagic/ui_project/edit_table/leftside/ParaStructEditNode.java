package com.qwaecd.paramagic.ui_project.edit_table.leftside;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import com.qwaecd.paramagic.ui.widget.UIScrollView;
import com.qwaecd.paramagic.ui_project.edit_table.SpellEditTableUI;
import com.qwaecd.paramagic.world.item.content.ParaCrystalItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParaStructEditNode extends UIScrollView {
    @Nullable
    private ParaPathNode rootPathNode;

    public ParaStructEditNode() {
        super(false);
        this.clipMod = ClipMod.NONE;
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        super.render(context);
    }

    public void updateFromParaData(@Nonnull ParaData paraData) {
        ParaComponentData root = paraData.rootComponent;
        ParaPathNode rootNode = new ParaPathNode(root.getComponentId());
        this.buildComponentNode(root, rootNode);
        if (this.rootPathNode != null) {
            this.removeChild(this.rootPathNode);
        }
        this.rootPathNode = rootNode;
        this.addChild(rootNode);
        this.rootPathNode.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
    }

    private void buildComponentNode(ParaComponentData parentData, ParaPathNode parentNode) {
        for (ParaComponentData child : parentData.getChildren()) {
            ParaPathNode childNode = new ParaPathNode(child.getComponentId());
            parentNode.addChild(childNode);
            this.buildComponentNode(child, childNode);
        }
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void recalculateContentExtent() {
        super.recalculateContentExtent();
    }

    void onContainerChanged(SpellEditTableUI mainUI, InventoryHolder container, UISlot slot) {
        ItemStack item = slot.getItem();
        if (!(item.getItem() instanceof ParaCrystalItem)) {
            this.removePathNode();
            return;
        }
        if (!mainUI.getContainerNode().isItemStack(item)) {
            this.removePathNode();
            return;
        }
        ParaCrystalData crystalData = CrystalComponentUtils.getComponentFromItemStack(item);
        if (crystalData == null) {
            this.removePathNode();
            return;
        }
        ParaData paraData = crystalData.getParaData();
        this.updateFromParaData(paraData);
    }

    @Override
    public void removeChild(UINode child) {
        super.removeChild(child);
    }

    private void removePathNode() {
        if (this.rootPathNode != null) {
            this.removeChild(this.rootPathNode);
            this.rootPathNode = null;
        }
    }
}
