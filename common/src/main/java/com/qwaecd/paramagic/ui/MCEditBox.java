package com.qwaecd.paramagic.ui;

import com.qwaecd.paramagic.ui.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class MCEditBox extends EditBox {
    public MCEditBox(int x, int y, int width, int height, Component message) {
        super(Minecraft.getInstance().font, x, y, width, height, message);
//        this.setBordered(false);
    }

    public void resize(Rect rect) {
        this.setX((int) rect.x);
        this.setY((int) rect.y);
        this.setWidth((int) rect.w);
        this.height = (int) rect.h;
    }

    public void setWH(int width, int height) {
        this.setWidth(width);
        this.height = height;
    }
}
