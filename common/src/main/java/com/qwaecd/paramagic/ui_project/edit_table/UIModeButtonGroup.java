package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui_project.edit_table.leftside.SideBar;

import javax.annotation.Nonnull;

public class UIModeButtonGroup extends UINode {
    private final SpellEditTableUI mainUI;
    private final ChangeButton paraSelect;
    private final ChangeButton crystalEdit;
    private final SideBar sideBar;

    public UIModeButtonGroup(SideBar sideBar, SpellEditTableUI spellEditTableUI) {
        this.mainUI = spellEditTableUI;
        this.sideBar = sideBar;
        this.paraSelect = new ChangeButton();
        this.crystalEdit = new ChangeButton();
        this.addChild(this.paraSelect);
        this.addChild(this.crystalEdit);

        this.paraSelect.addListener(
                AllUIEvents.MOUSE_CLICK,
                EventPhase.CAPTURING,
                this::handleClick
        );
        this.crystalEdit.addListener(
                AllUIEvents.MOUSE_CLICK,
                EventPhase.CAPTURING,
                this::handleClick
        );
        this.paraSelect.addListener(
                AllUIEvents.MOUSE_DOUBLE_CLICK,
                EventPhase.CAPTURING,
                this::handleDoubleClick
        );
        this.crystalEdit.addListener(
                AllUIEvents.MOUSE_DOUBLE_CLICK,
                EventPhase.CAPTURING,
                this::handleDoubleClick
        );

        this.localRect.setWH(ChangeButton.BUTTON_SIZE + 4.0f, ChangeButton.BUTTON_SIZE);
    }

    private void handleClick(UIEventContext<MouseClick> context) {
        UINode targetNode = context.targetNode;
        if (targetNode == this.paraSelect) {
            if (this.sideBar.getBarState() != BarState.PARA_SELECT) {
                this.mainUI.changeBarState(BarState.PARA_SELECT);
                this.crystalEdit.setPressed(false);
                context.consume();
            }
        } else if (targetNode == this.crystalEdit) {
            if (this.sideBar.getBarState() != BarState.CRYSTAL_EDIT) {
                this.mainUI.changeBarState(BarState.CRYSTAL_EDIT);
                this.paraSelect.setPressed(false);
                context.consume();
            }
        }
    }

    private void handleDoubleClick(UIEventContext<DoubleClick> context) {
        this.handleClick(UIEventContext.upcast(AllUIEvents.MOUSE_CLICK, context));
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        this.crystalEdit.localRect.setXY(0.0f, 0.0f);
        this.paraSelect.localRect.setXY(ChangeButton.BUTTON_SIZE + 4.0f, 0.0f);
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    public void render(@Nonnull UIRenderContext context) {
        super.render(context);
    }
}
