package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.ui.UIColor;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Deque;

public class UIRenderContext {
    @Nonnull
    private final UIRenderBackend backend;
    public final float deltaTime;
    private final Deque<Rect> clipStack;
    public final int mouseX;
    public final int mouseY;

    public UIRenderContext(@Nonnull UIRenderBackend backend, float deltaTime, int mouseX, int mouseY) {
        this.backend = backend;
        this.deltaTime = deltaTime;
        this.clipStack = new ArrayDeque<>();
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public void pushClipRect(@Nonnull Rect rect) {
        this.clipStack.push(rect);
        this.backend.pushClipRect(rect);
    }

    public void popClipRect() {
        if (!this.clipStack.isEmpty()) {
            this.clipStack.pop();
            this.backend.popClipRect();
        }
    }

    public void drawQuad(Rect rect, UIColor color) {
        this.backend.drawQuad(rect, color);
    }
}
