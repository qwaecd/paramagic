package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.io.mouse.MouseButton;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.UILabel;
import net.minecraft.client.gui.Font;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ColorRgbaEditorNode extends UINode {
    static final float ROW_GAP = 2.0f;
    static final float INPUT_HEIGHT = 14.0f;
    static final float LABEL_HEIGHT = 10.0f;
    static final float COMPONENT_GAP = 4.0f;

    private static final float PICKER_GAP = 4.0f;
    private static final float HUE_WIDTH = 12.0f;
    private static final float ALPHA_HEIGHT = 10.0f;
    private static final float PREVIEW_SIZE = 24.0f;
    private static final float PICKER_SIZE = 96.0f;
    private static final int CHECKER_LIGHT = UIColor.fromRGBA(200, 200, 200, 255);
    private static final int CHECKER_DARK = UIColor.fromRGBA(140, 140, 140, 255);
    private static final int OUTLINE_COLOR = UIColor.fromRGBA(30, 30, 30, 255);
    private static final int INDICATOR_LIGHT = UIColor.fromRGBA(255, 255, 255, 255);
    private static final int INDICATOR_DARK = UIColor.fromRGBA(0, 0, 0, 255);

    private final UILabel titleLabel;
    private final UILabel[] componentLabels;
    private final TypingBox[] componentBoxes;

    private final Rect svRect;
    private final Rect hueRect;
    private final Rect alphaRect;
    private final Rect previewRect;

    private final Vector4f rgba;
    private float hue;
    private float saturation;
    private float value;
    private float alpha;

    @Nullable
    private Consumer<Vector4f> changeListener;

    private DragTarget dragTarget = DragTarget.NONE;

    public ColorRgbaEditorNode() {
        // 颜色标题文本由 UILabel 子节点自己渲染，不在本节点的 render() 中手工绘制。
        this.titleLabel = createLabel("Color:");
        // RGBA 分量标签同样由 UILabel 子节点负责渲染。
        this.componentLabels = new UILabel[]{
                createLabel("R:"),
                createLabel("G:"),
                createLabel("B:"),
                createLabel("A:")
        };
        // RGBA 数值输入框由 TypingBox 原生控件节点自行渲染。
        this.componentBoxes = new TypingBox[]{
                createBox(),
                createBox(),
                createBox(),
                createBox()
        };

        this.svRect = new Rect();
        this.hueRect = new Rect();
        this.alphaRect = new Rect();
        this.previewRect = new Rect();

        this.rgba = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.hue = 0.0f;
        this.saturation = 0.0f;
        this.value = 1.0f;
        this.alpha = 1.0f;

        for (int i = 0; i < this.componentBoxes.length; i++) {
            final int index = i;
            this.componentBoxes[i].setFocusChangeListener(focused -> {
                if (!focused) {
                    this.commitComponentBox(index);
                }
            });
        }

        this.syncBoxesFromColor();
    }

    public void setChangeListener(@Nullable Consumer<Vector4f> listener) {
        this.changeListener = listener;
    }

    public void setColor(@Nonnull Vector4f color) {
        this.rgba.set(
                clamp01(color.x),
                clamp01(color.y),
                clamp01(color.z),
                clamp01(color.w)
        );
        this.syncHsvaFromRgba();
        this.syncBoxesFromColor();
    }

    public void layoutContent(Font font, float contentW) {
        float y = 0.0f;
        this.titleLabel.localRect.setXY(0.0f, y);
        y += LABEL_HEIGHT + 1.0f;

        float pickerSize = Math.min(PICKER_SIZE, Math.max(72.0f, contentW - HUE_WIDTH - PICKER_GAP));
        this.svRect.set(0.0f, y, pickerSize, pickerSize);
        this.hueRect.set(pickerSize + PICKER_GAP, y, HUE_WIDTH, pickerSize);
        y += pickerSize + ROW_GAP;

        this.alphaRect.set(0.0f, y, pickerSize + PICKER_GAP + HUE_WIDTH, ALPHA_HEIGHT);
        y += ALPHA_HEIGHT + ROW_GAP;

        this.previewRect.set(0.0f, y, PREVIEW_SIZE, PREVIEW_SIZE);

        int compLabelW = font.width("A:") + 1;
        float textVOffset = (INPUT_HEIGHT - font.lineHeight) / 2.0f;
        float boxesX = PREVIEW_SIZE + COMPONENT_GAP;
        float boxesW = contentW - boxesX;
        float boxW = (boxesW - this.componentBoxes.length * compLabelW - (this.componentBoxes.length - 1) * COMPONENT_GAP)
                / this.componentBoxes.length;
        float x = boxesX;
        for (int i = 0; i < this.componentBoxes.length; i++) {
            this.componentLabels[i].localRect.setXY(x, y + textVOffset);
            this.componentBoxes[i].localRect.set(x + compLabelW, y, boxW, INPUT_HEIGHT);
            x += compLabelW + boxW + COMPONENT_GAP;
        }

        this.localRect.h = y + Math.max(PREVIEW_SIZE, INPUT_HEIGHT);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        if (!this.isVisible()) {
            return;
        }

        Rect worldSv = toWorldRect(this.svRect);
        Rect worldHue = toWorldRect(this.hueRect);
        Rect worldAlpha = toWorldRect(this.alphaRect);
        Rect worldPreview = toWorldRect(this.previewRect);

        Vector3f hueColor = hsvToRgb(this.hue, 1.0f, 1.0f);
        int pureHue = UIColor.fromRGBA4f(hueColor.x, hueColor.y, hueColor.z, 1.0f);

        // SV 主选色面板：先画当前 hue 的纯色底图。
        context.drawQuad(worldSv, pureHue);
        // 再叠加一层从左到右的白色 -> 透明渐变，用来降低 saturation。
        context.fillBilinearGradient(
                worldSv,
                UIColor.WHITE.color,
                UIColor.fromRGBA(255, 255, 255, 0),
                UIColor.fromRGBA(255, 255, 255, 0),
                UIColor.WHITE.color
        );
        // 最后叠加一层从上到下的透明 -> 黑色渐变，用来降低 value。
        context.fillBilinearGradient(
                worldSv,
                UIColor.fromRGBA(0, 0, 0, 0),
                UIColor.fromRGBA(0, 0, 0, 0),
                UIColor.BLACK.color,
                UIColor.BLACK.color
        );
        // SV 面板外框。
        context.renderOutline(worldSv, OUTLINE_COLOR);

        // 右侧 Hue 色相条。
        renderHueStrip(context, worldHue);
        context.renderOutline(worldHue, OUTLINE_COLOR);

        // Alpha 滑条底部的棋盘格背景，用来表达透明度。
        renderCheckerboard(context, worldAlpha, 4);
        int leftAlpha = UIColor.fromRGBA4f(this.rgba.x, this.rgba.y, this.rgba.z, 0.0f);
        int rightAlpha = UIColor.fromRGBA4f(this.rgba.x, this.rgba.y, this.rgba.z, 1.0f);
        // Alpha 滑条本体：同一 RGB 从透明到不透明的渐变覆盖。
        context.fillBilinearGradient(worldAlpha, leftAlpha, rightAlpha, rightAlpha, leftAlpha);
        context.renderOutline(worldAlpha, OUTLINE_COLOR);

        // 预览块底部的棋盘格背景。
        renderCheckerboard(context, worldPreview, 4);
        // 当前颜色预览块本体。
        context.drawQuad(worldPreview, UIColor.fromRGBA4f(this.rgba.x, this.rgba.y, this.rgba.z, this.rgba.w));
        context.renderOutline(worldPreview, OUTLINE_COLOR);

        // 三处交互指示器：SV 圆框、Hue 横线、Alpha 竖线。
        renderIndicators(context, worldSv, worldHue, worldAlpha);
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        if (!MouseButton.LEFT.is(context.event.button)) {
            return;
        }

        DragTarget target = resolveDragTarget((float) context.event.mouseX, (float) context.event.mouseY);
        if (target == DragTarget.NONE) {
            return;
        }

        this.dragTarget = target;
        context.getManager().captureNode(this);
        this.updateFromPointer((float) context.event.mouseX, (float) context.event.mouseY);
        context.consumeAndStopPropagation();
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY, MouseStateMachine mouseState) {
        if (this.dragTarget == DragTarget.NONE) {
            return;
        }
        this.updateFromPointer((float) mouseX, (float) mouseY);
    }

    @Override
    protected void onMouseRelease(UIEventContext<MouseRelease> context) {
        if (this.dragTarget == DragTarget.NONE) {
            return;
        }
        context.getManager().releaseCapture();
        this.dragTarget = DragTarget.NONE;
        context.consumeAndStopPropagation();
    }

    private void commitComponentBox(int index) {
        String text = this.componentBoxes[index].getText();
        try {
            int value255 = clamp255(Integer.parseInt(text));
            switch (index) {
                case 0 -> this.rgba.x = value255 / 255.0f;
                case 1 -> this.rgba.y = value255 / 255.0f;
                case 2 -> this.rgba.z = value255 / 255.0f;
                case 3 -> this.rgba.w = value255 / 255.0f;
                default -> {
                    return;
                }
            }
            this.syncHsvaFromRgba();
            this.syncBoxesFromColor();
            this.fireChange();
        } catch (NumberFormatException e) {
            this.syncBoxesFromColor();
        }
    }

    private void updateFromPointer(float mouseX, float mouseY) {
        float localX = mouseX - this.worldRect.x;
        float localY = mouseY - this.worldRect.y;

        switch (this.dragTarget) {
            case SV_SQUARE -> {
                this.saturation = clamp01((localX - this.svRect.x) / this.svRect.w);
                this.value = 1.0f - clamp01((localY - this.svRect.y) / this.svRect.h);
            }
            case HUE_STRIP -> this.hue = clamp01((localY - this.hueRect.y) / this.hueRect.h);
            case ALPHA_SLIDER -> this.alpha = clamp01((localX - this.alphaRect.x) / this.alphaRect.w);
            case NONE -> {
                return;
            }
        }

        this.syncRgbaFromHsva();
        this.syncBoxesFromColor();
        this.fireChange();
    }

    private DragTarget resolveDragTarget(float mouseX, float mouseY) {
        float localX = mouseX - this.worldRect.x;
        float localY = mouseY - this.worldRect.y;
        if (contains(this.svRect, localX, localY)) return DragTarget.SV_SQUARE;
        if (contains(this.hueRect, localX, localY)) return DragTarget.HUE_STRIP;
        if (contains(this.alphaRect, localX, localY)) return DragTarget.ALPHA_SLIDER;
        return DragTarget.NONE;
    }

    private void syncHsvaFromRgba() {
        this.alpha = clamp01(this.rgba.w);

        float r = clamp01(this.rgba.x);
        float g = clamp01(this.rgba.y);
        float b = clamp01(this.rgba.z);
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        if (delta > 0.00001f) {
            if (max == r) {
                this.hue = (((g - b) / delta) % 6.0f) / 6.0f;
            } else if (max == g) {
                this.hue = (((b - r) / delta) + 2.0f) / 6.0f;
            } else {
                this.hue = (((r - g) / delta) + 4.0f) / 6.0f;
            }
            if (this.hue < 0.0f) {
                this.hue += 1.0f;
            }
        }

        this.value = max;
        this.saturation = max <= 0.00001f ? 0.0f : delta / max;
    }

    private void syncRgbaFromHsva() {
        Vector3f rgb = hsvToRgb(this.hue, this.saturation, this.value);
        this.rgba.set(rgb.x, rgb.y, rgb.z, this.alpha);
    }

    private void syncBoxesFromColor() {
        this.componentBoxes[0].setText(String.valueOf(to255(this.rgba.x)));
        this.componentBoxes[1].setText(String.valueOf(to255(this.rgba.y)));
        this.componentBoxes[2].setText(String.valueOf(to255(this.rgba.z)));
        this.componentBoxes[3].setText(String.valueOf(to255(this.rgba.w)));
    }

    private void fireChange() {
        if (this.changeListener != null) {
            this.changeListener.accept(new Vector4f(this.rgba));
        }
    }

    private void renderHueStrip(UIRenderContext context, Rect rect) {
        final int segments = 6;
        for (int i = 0; i < segments; i++) {
            float startHue = (float) i / segments;
            float endHue = (float) (i + 1) / segments;
            Vector3f top = hsvToRgb(startHue, 1.0f, 1.0f);
            Vector3f bottom = hsvToRgb(endHue, 1.0f, 1.0f);
            float segmentY = rect.y + rect.h * i / segments;
            float segmentH = rect.h / segments;
            int topColor = UIColor.fromRGBA4f(top.x, top.y, top.z, 1.0f);
            int bottomColor = UIColor.fromRGBA4f(bottom.x, bottom.y, bottom.z, 1.0f);
            // Hue 色相条由多个纵向小段拼起来，每段内部做上下线性渐变。
            context.fillBilinearGradient(
                    rect.x,
                    segmentY,
                    rect.w,
                    segmentH,
                    topColor,
                    topColor,
                    bottomColor,
                    bottomColor
            );
        }
    }

    private void renderIndicators(UIRenderContext context, Rect worldSv, Rect worldHue, Rect worldAlpha) {
        // SV 面板中的选中点，用双层描边强调当前位置。
        float svX = worldSv.x + this.saturation * worldSv.w;
        float svY = worldSv.y + (1.0f - this.value) * worldSv.h;
        context.renderOutline((int) svX - 2, (int) svY - 2, 5, 5, INDICATOR_DARK);
        context.renderOutline((int) svX - 1, (int) svY - 1, 3, 3, INDICATOR_LIGHT);

        // Hue 色相条中的横向游标。
        int hueY = (int) (worldHue.y + this.hue * worldHue.h);
        context.hLine((int) worldHue.x - 1, (int) (worldHue.x + worldHue.w), hueY, INDICATOR_DARK);
        context.hLine((int) worldHue.x, (int) (worldHue.x + worldHue.w - 1), hueY + 1, INDICATOR_LIGHT);

        // Alpha 滑条中的纵向游标。
        int alphaX = (int) (worldAlpha.x + this.alpha * worldAlpha.w);
        context.vLine(alphaX, (int) worldAlpha.y - 1, (int) (worldAlpha.y + worldAlpha.h), INDICATOR_DARK);
        context.vLine(alphaX + 1, (int) worldAlpha.y, (int) (worldAlpha.y + worldAlpha.h - 1), INDICATOR_LIGHT);
    }

    private static void renderCheckerboard(UIRenderContext context, Rect rect, int cellSize) {
        // 透明背景棋盘格：供 Alpha 滑条和预览块复用。
        int cols = Math.max(1, (int) Math.ceil(rect.w / cellSize));
        int rows = Math.max(1, (int) Math.ceil(rect.h / cellSize));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int color = ((row + col) & 1) == 0 ? CHECKER_LIGHT : CHECKER_DARK;
                float x = rect.x + col * cellSize;
                float y = rect.y + row * cellSize;
                float w = Math.min(cellSize, rect.x + rect.w - x);
                float h = Math.min(cellSize, rect.y + rect.h - y);
                context.fill(x, y, x + w, y + h, color);
            }
        }
    }

    @Nonnull
    private Rect toWorldRect(@Nonnull Rect localRect) {
        return new Rect(
                this.worldRect.x + localRect.x,
                this.worldRect.y + localRect.y,
                localRect.w,
                localRect.h
        );
    }

    @Nonnull
    private UILabel createLabel(@Nonnull String text) {
        UILabel label = new UILabel(text);
        label.setHitTestable(false);
        this.addChild(label);
        return label;
    }

    @Nonnull
    private TypingBox createBox() {
        TypingBox box = new TypingBox();
        box.setMaxLength(3);
        box.localRect.setWH(0.0f, INPUT_HEIGHT);
        this.addChild(box);
        return box;
    }

    private static boolean contains(Rect rect, float x, float y) {
        return x >= rect.x && x < rect.x + rect.w && y >= rect.y && y < rect.y + rect.h;
    }

    private static int to255(float value) {
        return clamp255(Math.round(clamp01(value) * 255.0f));
    }

    private static int clamp255(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    @Nonnull
    private static Vector3f hsvToRgb(float hue, float saturation, float value) {
        float h = (clamp01(hue) * 6.0f) % 6.0f;
        float c = value * saturation;
        float x = c * (1.0f - Math.abs(h % 2.0f - 1.0f));
        float m = value - c;

        float r;
        float g;
        float b;
        if (h < 1.0f) {
            r = c;
            g = x;
            b = 0.0f;
        } else if (h < 2.0f) {
            r = x;
            g = c;
            b = 0.0f;
        } else if (h < 3.0f) {
            r = 0.0f;
            g = c;
            b = x;
        } else if (h < 4.0f) {
            r = 0.0f;
            g = x;
            b = c;
        } else if (h < 5.0f) {
            r = x;
            g = 0.0f;
            b = c;
        } else {
            r = c;
            g = 0.0f;
            b = x;
        }
        return new Vector3f(r + m, g + m, b + m);
    }

    private enum DragTarget {
        NONE,
        SV_SQUARE,
        HUE_STRIP,
        ALPHA_SLIDER
    }
}
