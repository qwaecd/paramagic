package com.qwaecd.paramagic.ui;

import com.qwaecd.paramagic.ui.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MCEditBox extends EditBox {
    @Nullable
    private Consumer<Boolean> focusChangeListener;

    public MCEditBox(int x, int y, int width, int height, Component message) {
        super(Minecraft.getInstance().font, x, y, width, height, message);
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

    public void setFocusChangeListener(@Nullable Consumer<Boolean> listener) {
        this.focusChangeListener = listener;
    }

    @Override
    public void setFocused(boolean focused) {
        boolean wasFocused = this.isFocused();
        super.setFocused(focused);
        if (wasFocused != focused && this.focusChangeListener != null) {
            this.focusChangeListener.accept(focused);
        }
    }
}
