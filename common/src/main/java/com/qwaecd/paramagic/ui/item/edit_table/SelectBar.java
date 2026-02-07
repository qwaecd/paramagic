package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.UINode;

public class SelectBar extends UINode {

    public SelectBar() {
        this.localRect.setWH(32, 180);
        this.layoutParams.enable();
        this.backgroundColor = UIColor.of(172, 122, 52, 255);
    }
}
