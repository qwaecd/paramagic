package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.util.Sprite;

public class EditTableSprite extends Sprite {
    public final int spriteOffsetX;
    public final int spriteOffsetY;

    public EditTableSprite(int u, int v, int width, int height, int spriteOffsetX, int spriteOffsetY) {
        super(ModRL.inModSpace("textures/gui/edit_table.png"), u, v, width, height, 320, 256);
        this.spriteOffsetX = spriteOffsetX;
        this.spriteOffsetY = spriteOffsetY;
    }
}
