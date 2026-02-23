package com.qwaecd.paramagic.ui_project.edit_table.leftside;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.inventory.ContainerHolder;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import com.qwaecd.paramagic.ui_project.edit_table.BarState;
import com.qwaecd.paramagic.ui_project.edit_table.SpellEditTableUI;

import javax.annotation.Nonnull;

public class SideBar extends UINode {
    @Nonnull
    private final ParaSelectBar paraSelectBar;
    @Nonnull
    private final ParaStructEditNode structEditNode;

    @Nonnull
    private BarState state;

    public SideBar() {
        this.state = BarState.NULL;
        this.clipMod = ClipMod.RECT;
        this.paraSelectBar = new ParaSelectBar();
        this.structEditNode = new ParaStructEditNode();
    }

    /**
     * 在 SpellEditTableUI.init() 中调用，用于初始化容器内已有物品的数据。
     */
    public void initContainer(SpellEditTableUI mainUI, ContainerHolder container, UISlot containerSlot) {
        this.structEditNode.onContainerChanged(mainUI, container, containerSlot);
    }

    public void changeToParaSelectBar() {
        if (this.state == BarState.CRYSTAL_EDIT) {
            this.removeChild(this.structEditNode);
        }
        if (this.state != BarState.PARA_SELECT) {
            this.addChild(this.paraSelectBar);
        }
        this.state = BarState.PARA_SELECT;
    }

    public void changeToCrystalEdit() {
        if (this.state == BarState.PARA_SELECT) {
            this.removeChild(this.paraSelectBar);
        }
        if (this.state != BarState.CRYSTAL_EDIT) {
            this.addChild(this.structEditNode);
        }
        this.state = BarState.CRYSTAL_EDIT;
    }

    public void changeToNull() {
        if (this.state == BarState.PARA_SELECT) {
            this.removeChild(this.paraSelectBar);
        } else if (this.state == BarState.CRYSTAL_EDIT) {
            this.removeChild(this.structEditNode);
        }
        this.state = BarState.NULL;
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        this.localRect.set(
                0.0f, 0.0f,
                94.3f, this.getWindowHeight() / this.getGuiScale()
        );
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        super.render(context);
    }

    @Nonnull
    public BarState getBarState() {
        return this.state;
    }

    public void onContainerChanged(SpellEditTableUI mainUI, InventoryHolder container, UISlot slot) {
        this.structEditNode.onContainerChanged(mainUI, container, slot);
    }
}
