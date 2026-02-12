package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.CanvasNode;

public class EditWindow extends UINode {

    public EditWindow() {
        this.localRect.setWH(220, 180);
        this.backgroundColor = UIColor.of(129, 64, 0, 255);
        this.layoutParams.center();
        this.clipMod = ClipMod.RECT;
        this.addCanvas();
    }

    private void addCanvas() {
        this.addChild(new CanvasNode());
    }
}
