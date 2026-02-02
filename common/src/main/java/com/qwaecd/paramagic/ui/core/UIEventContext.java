package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.ui.io.mouse.MouseEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UIEventContext {
    @Nonnull
    public final UIManager manager;

    @Nullable
    public final MouseEvent mouseEvent;

    private boolean consumed = false;

    public UIEventContext(@Nonnull UIManager manager) {
        this.manager = manager;
        this.mouseEvent = null;
    }

    public UIEventContext(@Nonnull UIManager manager, @Nonnull MouseEvent mouseEvent) {
        this.manager = manager;
        this.mouseEvent = mouseEvent;
    }

    public boolean isMouseEvent() {
        return this.mouseEvent != null;
    }

    @Nonnull
    public UIManager getManager() {
        return this.manager;
    }

    public boolean isConsumed() {
        return this.consumed;
    }

    // 你要干什么?
    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public void consume() {
        this.consumed = true;
    }
}
