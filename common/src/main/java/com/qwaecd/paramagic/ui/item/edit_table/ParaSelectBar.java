package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.ui.widget.ItemNode;
import com.qwaecd.paramagic.ui.widget.UIPanel;

public class ParaSelectBar extends SelectBar {
    protected final UIPanel panel;

    public ParaSelectBar() {
        // 左对齐稍微偏右
        this.layoutParams.set(0.01f, 0.5f, 0.0f, 0.5f);

        this.panel = new UIPanel(5, 1, this.localRect.w, true, 4, 8, ItemNode.CELL_SIZE);
        for (int i = 0; i < 5; i++) {
            this.panel.addChild(new ItemNode());
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
