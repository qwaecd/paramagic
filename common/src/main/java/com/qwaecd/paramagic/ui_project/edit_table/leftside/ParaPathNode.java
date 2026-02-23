package com.qwaecd.paramagic.ui_project.edit_table.leftside;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.util.UIColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParaPathNode extends UINode {
    private static final float childrenIndentation = 8.0f;
    private static final float defaultNodeWidth = 16.0f;
    private static final float defaultNodeHeight = 8.0f;

    @Nullable
    private Component path;

    public ParaPathNode(@Nullable Component path) {
        this.path = path;
    }

    public ParaPathNode(@Nonnull String path) {
        this(Component.literal(path));
    }

    public void setParaPath(Component path) {
        this.path = path;
    }

    private float getNodeHeight() {
        if (this.path != null) {
            Font font = Minecraft.getInstance().font;
            return font.lineHeight;
        }
        return defaultNodeHeight;
    }

    private float getSubtreeHeight() {
        float totalHeight = this.getNodeHeight();
        for (UINode child : this.children) {
            if (child instanceof ParaPathNode paraPathNode) {
                totalHeight += paraPathNode.getSubtreeHeight();
            } else {
                totalHeight += child.localRect.h;
            }
        }
        return totalHeight;
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
    }

    @Override
    protected void onDoubleClick(UIEventContext<DoubleClick> context) {
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        float nodeWidth = defaultNodeWidth;
        float nodeHeight = defaultNodeHeight;
        if (this.path != null) {
            Font font = Minecraft.getInstance().font;
            nodeWidth = font.width(this.path);
            nodeHeight = font.lineHeight;
        }
        this.localRect.setWH(nodeWidth, nodeHeight);

        if (this.parent == null) {
            this.localRect.setXY(0.0f, 0.0f);
        } else {
            this.localRect.setXY(childrenIndentation, this.localRect.y);
        }

        float nextChildY = nodeHeight;
        for (UINode child : this.children) {
            child.localRect.setXY(childrenIndentation, nextChildY);
            if (child instanceof ParaPathNode paraPathNode) {
                nextChildY += paraPathNode.getSubtreeHeight();
            } else {
                nextChildY += child.localRect.h;
            }
        }
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        if (this.path != null) {
            final float offsetX = 2.0f;
            final float offsetY = 1.0f;
            int lineHeight = context.getLineHeight();
            if (this.localRect.w != lineHeight + offsetY * 2.0f) {
                this.localRect.w = lineHeight + offsetY * 2.0f;
            }
            context.drawText(this.path, this.worldRect.x + offsetX, this.worldRect.y + offsetY, UIColor.WHITE);
        }
        context.fill(worldRect.x, worldRect.y, worldRect.x + worldRect.w, worldRect.y + worldRect.h, UIColor.fromRGBA(127, 127, 127, 50));
    }
}
