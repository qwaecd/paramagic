package com.qwaecd.paramagic.ui.core;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class UINode {
    private final List<UINode> children;
    @Nullable
    private UINode parent;
    private boolean visible;
    @Setter
    @Getter
    @Nonnull
    private ClipMod clipMod;

    @Setter
    @Getter
    @Nonnull
    private SizeMode sizeMode;
    
    /**
     * 相对父节点的位置和尺寸
     */
    @Getter
    @Nonnull
    public final Rect localRect;

    /**
     * 缓存的屏幕绝对矩形（layout 后写入）
     */
    @Getter
    @Nonnull
    public final Rect worldRect;

    public UINode() {
        this.children = new ArrayList<>();
        this.parent = null;
        this.visible = true;
        this.clipMod = ClipMod.NONE;
        this.sizeMode = SizeMode.FIXED;
        this.localRect = new Rect();
        this.worldRect = new Rect();
    }

    public UINode(@Nonnull UINode parent) {
        this.children = new ArrayList<>();
        this.parent = parent;
        this.visible = true;
        this.clipMod = ClipMod.NONE;
        this.sizeMode = SizeMode.FIXED;
        this.localRect = new Rect();
        this.worldRect = new Rect();

        parent.addChild(this);
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<UINode> getChildren() {
        return this.children;
    }

    @Nullable
    public UINode getParent() {
        return this.parent;
    }

    public void addChild(UINode child) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }

        this.children.add(child);
        child.parent = this;
    }

    public void removeChild(UINode child) {
        if (this.children.remove(child)) {
            child.parent = null;
        }
    }

    /**
     * 计算此节点及其子节点的屏幕绝对坐标
     * @param parentX 父节点的屏幕X坐标
     * @param parentY 父节点的屏幕Y坐标
     * @param parentW 父节点的宽度
     * @param parentH 父节点的高度
     */
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        this.worldRect.x = parentX + this.localRect.x;
        this.worldRect.y = parentY + this.localRect.y;
        
        this.computeSize(parentW, parentH);
        
        for (UINode child : this.children) {
            child.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
        }
    }

    private void computeSize(float parentW, float parentH) {
        switch (this.sizeMode) {
            case FIXED -> {
                this.worldRect.w = this.localRect.w;
                this.worldRect.h = this.localRect.h;
            }
            case FILL -> {
                this.worldRect.w = parentW;
                this.worldRect.h = parentH;
            }
            case FILL_WIDTH -> {
                this.worldRect.w = parentW;
                this.worldRect.h = this.localRect.h;
            }
            case FILL_HEIGHT -> {
                this.worldRect.w = this.localRect.w;
                this.worldRect.h = parentH;
            }
            default -> throw new IllegalStateException("Unexpected value: " + this.sizeMode);
        }
    }

    public void render(@Nonnull UIRenderContext context) {
    }

    public void renderTree(UIRenderContext context) {
        boolean hasClip = (this.clipMod == ClipMod.RECT);
        if (hasClip) {
            context.pushClipRect(this.worldRect);
        }

        if (this.isVisible()) {
            this.render(context);
        }

        for (UINode child : this.children) {
            child.renderTree(context);
        }

        if (hasClip) {
            context.popClipRect();
        }
    }

    public boolean hitTest(float x, float y) {
        if (!this.isVisible()) {
            return false;
        }
        return x >= this.worldRect.x && x < this.worldRect.x + this.worldRect.w
            && y >= this.worldRect.y && y < this.worldRect.y + this.worldRect.h;
    }

    /**
     * 递归查找命中的最上层节点（倒序遍历子节点，后添加的在上层）
     * @param x 屏幕X坐标
     * @param y 屏幕Y坐标
     * @return 命中的节点，如果没有命中返回null
     */
    @Nullable
    public UINode findHitNode(float x, float y) {
        if (!this.hitTest(x, y)) {
            return null;
        }

        // 后画的在上层，先命中
        for (int i = this.children.size() - 1; i >= 0; i--) {
            UINode child = this.children.get(i);
            UINode hit = child.findHitNode(x, y);
            if (hit != null) {
                return hit;
            }
        }
        
        return this;
    }
}
