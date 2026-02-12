package com.qwaecd.paramagic.ui.api;

import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.Sprite;
import com.qwaecd.paramagic.ui.util.UIColor;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Deque;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class UIRenderContext {
    @Getter
    @Nonnull
    public  final UIManager manager;
    // 虽然这么做会破坏封装性, 但就 render tooltip 这一次 :(
    private final GuiGraphics guiGraphics;

    @Nonnull
    private final UIRenderBackend backend;

    public  final float deltaTime;

    private final Deque<Rect> clipStack;

    public  final int mouseX;
    public  final int mouseY;

    public UIRenderContext(
            @Nonnull UIManager manager,
            @Nonnull GuiGraphics guiGraphics,
            @Nonnull UIRenderBackend backend,
            float deltaTime,
            int mouseX, int mouseY
    )
    {
        this.manager = manager;
        this.guiGraphics = guiGraphics;
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
            return new Rect(0.0f, 0.0f, 0.0f, 0.0f);
        }
    }

    public void popClipRect() {
        if (!this.clipStack.isEmpty()) {
            this.clipStack.pop();
            this.backend.popClipRect();
        }
    }

    public void drawQuad(Rect rect, UIColor color) {
        this.backend.drawQuad(rect, color.color);
    }

    public void fill(int minX, int minY, int maxX, int maxY, int color) {
        this.backend.fill(minX, minY, maxX, maxY, color);
    }

    public void fill(float minX, float minY, float maxX, float maxY, int color) {
        this.backend.fill( (int) minX,  (int) minY,  (int) maxX,  (int) maxY, color);
    }

    public void renderOutline(Rect rect, UIColor color) {
        this.backend.renderOutline(rect, color.color);
    }

    public void renderOutline(int x, int y, int w, int h, UIColor color) {
        this.backend.renderOutline(x, y, w, h, color.color);
    }

    public int drawText(Component text, int x, int y, UIColor color, boolean dropShadow) {
        return this.backend.drawText(text, x, y, color.color, dropShadow);
    }

    public int drawText(String text, int x, int y, UIColor color, boolean dropShadow) {
        return this.backend.drawText(Component.literal(text), x, y, color.color, dropShadow);
    }

    public int drawText(Component text, float x, float y, UIColor color, boolean dropShadow) {
        return this.drawText(text, (int) x, (int) y, color, dropShadow);
    }

    public int drawText(String text, float x, float y, UIColor color, boolean dropShadow) {
        return this.drawText(Component.literal(text), (int) x, (int) y, color, dropShadow);
    }

    public int drawText(Component text, float x, float y, UIColor color) {
        return this.drawText(text, (int) x, (int) y, color, false);
    }

    public int drawText(String text, float x, float y, UIColor color) {
        return this.drawText(Component.literal(text), (int) x, (int) y, color, false);
    }

    public void drawCenteredText(Component text, float centerX, float y, UIColor color) {
        this.backend.drawCenteredText(text, centerX, y, color.color);
    }

    public int getTextWidth(String text) {
        return this.backend.getTextWidth(text);
    }

    public int getTextWidth(Component text) {
        return this.backend.getTextWidth(text);
    }

    public int getLineHeight() {
        return this.backend.getLineHeight();
    }

    public void renderSprite(Sprite sprite, int x, int y) {
        this.backend.renderSprite(sprite, x, y);
    }

    /**
     * 在指定位置渲染精灵图.
     * @param sprite 精灵图实例.
     * @param x 绘制区域左上角的 x 坐标.
     * @param y 绘制区域左上角的 y 坐标.
     */
    public void renderSprite(Sprite sprite, float x, float y) {
        this.renderSprite(sprite, (int) x, (int) y);
    }

    public void renderItem(ItemStack stack, int x, int y) {
        this.backend.renderItem(stack, x, y);
    }

    public void renderTooltipWithItem(@Nonnull ItemStack stack, int x, int y) {
        this.manager.getTooltipRenderer().renderTooltipWithItem(stack, this.guiGraphics, x, y);
    }

    // ------------ 以下是使用 int color 的重载方法 ------------

    public void drawQuad(Rect rect, int color) {
        this.backend.drawQuad(rect, color);
    }

    public void renderOutline(Rect rect, int color) {
        this.backend.renderOutline(rect, color);
    }

    public void renderOutline(int x, int y, int w, int h, int color) {
        this.backend.renderOutline(x, y, w, h, color);
    }

    public int drawText(Component text, int x, int y, int color, boolean dropShadow) {
        return this.backend.drawText(text, x, y, color, dropShadow);
    }

    public int drawText(String text, int x, int y, int color, boolean dropShadow) {
        return this.backend.drawText(Component.literal(text), x, y, color, dropShadow);
    }

    public int drawText(Component text, float x, float y, int color, boolean dropShadow) {
        return this.drawText(text, (int) x, (int) y, color, dropShadow);
    }

    public int drawText(String text, float x, float y, int color, boolean dropShadow) {
        return this.drawText(Component.literal(text), (int) x, (int) y, color, dropShadow);
    }

    public int drawText(Component text, float x, float y, int color) {
        return this.drawText(text, (int) x, (int) y, color, false);
    }

    public int drawText(String text, float x, float y, int color) {
        return this.drawText(Component.literal(text), (int) x, (int) y, color, false);
    }

    public void drawCenteredText(Component text, float centerX, float y, int color) {
        this.backend.drawCenteredText(text, centerX, y, color);
    }
}
