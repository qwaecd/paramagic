package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.util.Sprite;

public class EditTableSprite extends Sprite {
    public static final int TEX_W = 320;
    public static final int TEX_H = 320;
    public final int spriteOffsetX;
    public final int spriteOffsetY;

    public EditTableSprite(int u, int v, int width, int height, int spriteOffsetX, int spriteOffsetY) {
        super(ModRL.inModSpace("textures/gui/edit_table.png"), u, v, width, height, TEX_W, TEX_H);
        this.spriteOffsetX = spriteOffsetX;
        this.spriteOffsetY = spriteOffsetY;
    }
}
