package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.thaumaturgy.node.ParaNode;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.UIColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ParaTree 数据结构在 UI 下的可视化渲染体现.
 * @see com.qwaecd.paramagic.thaumaturgy.node.ParaTree
 */
public class PTTreeNode extends UINode {
    /**
     * 兄弟节点之间的水平间距. 该值不包含节点本身的宽度，即兄弟节点之间的距离为 hGap.
     */
    private static final int hGap = 8;
    /**
     * 父子节点之间的垂直间距. 该值不包含子节点本身的高度，即子节点与父节点之间的距离为 vGap.
     */
    private static final int vGap = 16;
    /**
     * 每个节点的宽高.
     */
    private static final int NODE_CELL_SIZE = ItemNode.CELL_SIZE;

    /**
     * 连接父子节点的线条颜色.
     */
    private static final UIColor lineColor = new UIColor(255, 255, 255, 255);

    @Nonnull
    private final ParaTree tree;
    @Nonnull
    private final PTTreeLayout layout;

    public PTTreeNode(@Nonnull ParaTree tree) {
        this.tree = tree;
        this.layout = new PTTreeLayout(tree, NODE_CELL_SIZE, hGap, vGap);
    }

    @Nonnull
    public ParaTree getTree() {
        return this.tree;
    }

    /**
     * 在给定屏幕空间鼠标坐标的情况下，寻找命中的节点.
     * @return 命中的 ParaNode，若没有命中任何节点则返回 null.
     */
    private float getCanvasZoom() {
        UINode parent = this.getParent();
        if (parent instanceof CanvasNode canvas) {
            return canvas.zoom;
        }
        return 1.0f;
    }

    @Nullable
    public ParaNode findNode(float mouseX, float mouseY) {
        float zoom = getCanvasZoom();
        float localX = (mouseX - this.worldRect.x) / zoom;
        float localY = (mouseY - this.worldRect.y) / zoom;
        float half = NODE_CELL_SIZE / 2.0f;
        for (PTTreeLayout.NodeEntry entry : this.layout.getNodeEntries()) {
            if (localX >= entry.x - half && localX <= entry.x + half
                    && localY >= entry.y - half && localY <= entry.y + half) {
                return entry.node;
            }
        }
        return null;
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        final float offsetX = this.parent == null ? 0.0f : this.parent.localRect.w * 0.5f + 16.0f;
        final float offsetY = this.parent == null ? 0.0f : this.parent.localRect.h * 0.5f + 16.0f;
        this.localRect.setXY(offsetX, offsetY);
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        float zoom = getCanvasZoom();
        float ox = this.worldRect.x;
        float oy = this.worldRect.y;
        int color = lineColor.color;

        // 绘制所有连接线段
        for (PTTreeLayout.EdgeSegment edge : this.layout.getEdgeSegments()) {
            float x1 = ox + edge.x1 * zoom;
            float y1 = oy + edge.y1 * zoom;
            float x2 = ox + edge.x2 * zoom;
            float y2 = oy + edge.y2 * zoom;
            if (x1 == x2) {
                context.vLine((int) x1, (int) Math.min(y1, y2) - 1, (int) Math.max(y1, y2), color);
            } else {
                context.hLine((int) Math.min(x1, x2), (int) Math.max(x1, x2), (int) y1, color);
            }
        }

        // 绘制所有节点
        for (PTTreeLayout.NodeEntry entry : this.layout.getNodeEntries()) {
            renderParaNode(context, entry.node, ox + entry.x * zoom, oy + entry.y * zoom);
        }
    }

    /**
     * 在给定位置可视化渲染一个 ParaNode.
     * @param paraNode 需要渲染的 ParaNode.
     * @param nodeX ParaNode 的中心点在屏幕空间的 X 坐标.
     * @param nodeY ParaNode 的中心点在屏幕空间的 Y 坐标.
     */
    protected void renderParaNode(@Nonnull UIRenderContext context, @Nonnull ParaNode paraNode, float nodeX, float nodeY) {
        renderOperator(context, paraNode.getOperator(), nodeX, nodeY);
        float half = NODE_CELL_SIZE / 2.0f * getCanvasZoom();
        context.fill(nodeX - half, nodeY - half, nodeX + half, nodeY + half, UIColor.fromRGBA(255, 0, 0, 255));
    }

    private void renderOperator(@Nonnull UIRenderContext context, @Nullable ParaOperator operator, float nodeX, float nodeY) {
    }
}
