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
        Rect clipRect;
        if (this.clipStack.peek() != null) {
            clipRect = crossRect(this.clipStack.peek(), rect);
        } else {
            clipRect = rect;
        }
        this.clipStack.push(clipRect);
        this.backend.pushClipRect(clipRect);
    }

    private static Rect crossRect(@Nonnull Rect a, @Nonnull Rect b) {
        float x1 = Math.max(a.x, b.x);
        float y1 = Math.max(a.y, b.y);
        float x2 = Math.min(a.x + a.w, b.x + b.w);
        float y2 = Math.min(a.y + a.h, b.y + b.h);
        if (x2 >= x1 && y2 >= y1) {
            return new Rect(x1, y1, x2 - x1, y2 - y1);
        } else {
            return new Rect(0, 0, 0, 0);
        }
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

    public void renderOutline(Rect rect, UIColor color) {
        this.backend.renderOutline(rect, color);
    }
}
