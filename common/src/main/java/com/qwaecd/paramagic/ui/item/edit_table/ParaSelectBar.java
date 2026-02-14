package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.UIPanel;
import com.qwaecd.paramagic.ui.widget.node.ItemNode;

public class ParaSelectBar extends UINode {
    protected final UIPanel panel;

    public ParaSelectBar() {
        this.localRect.setWH(32, 180);
        this.layoutParams.enable();
        this.backgroundColor = UIColor.of(172, 122, 52, 255);
        // 左对齐稍微偏右
        this.layoutParams.set(0.01f, 0.5f, 0.0f, 0.5f);

        this.panel = new UIPanel(5, 1, this.localRect.w, true, 4, 16, ItemNode.CELL_SIZE);
        for (int i = 0; i < 5; i++) {
            this.panel.addItemNode(new ItemNode());
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
