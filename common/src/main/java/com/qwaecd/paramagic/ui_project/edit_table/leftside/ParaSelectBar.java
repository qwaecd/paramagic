package com.qwaecd.paramagic.ui_project.edit_table.leftside;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.UIPanel;
import com.qwaecd.paramagic.ui.widget.node.ItemNode;
import com.qwaecd.paramagic.ui_project.edit_table.EditTableSprite;

public class ParaSelectBar extends UINode {
    protected final UIPanel panel;

    private static final EditTableSprite selectBar = new EditTableSprite(
            0, 0,
            40, 188,
            -4, -4
    );

    public ParaSelectBar() {
        this.localRect.setWH(32, 180);
        this.backgroundColor = UIColor.of(172, 122, 52, 255);

        this.panel = new UIPanel(5, 1, this.localRect.w, true, 4, 16, ItemNode.CELL_SIZE);
        for (int i = 0; i < 5; i++) {
            this.panel.addItemNode(new ItemNode());
        }
        this.addChild(this.panel);
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        // 将自己置于屏幕左边中间
        float windowH = this.getWindowHeight() / this.getGuiScale();
        this.localRect.setXY(
                8.0f,
                (windowH - this.localRect.h) / 2.0f
        );
        this.panel.localRect.setXY(
                (this.localRect.w - ItemNode.CELL_SIZE) / 2.0f,
                4.0f
        );
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void renderBackGround(UIRenderContext context) {
        context.renderSprite(selectBar, this.worldRect.x + selectBar.spriteOffsetX, this.worldRect.y + selectBar.spriteOffsetY);
    }
}
