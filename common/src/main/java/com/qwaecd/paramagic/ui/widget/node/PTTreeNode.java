package com.qwaecd.paramagic.ui.widget.node;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.thaumaturgy.node.ParaNode;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.world.item.ItemStack;

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

    private static final ItemStack testItem = new ItemStack(ModItems.EXPLOSION_WAND);

    public PTTreeNode(@Nonnull ParaTree tree) {
        this.tree = tree;
        this.layout = new PTTreeLayout(tree, NODE_CELL_SIZE, hGap, vGap);
    }

    @Override
    public void onMouseClick(UIEventContext<MouseClick> context) {
        if (context.isConsumed()) {
            return;
        }
        MouseClick event = context.event;
        PTTreeLayout.NodeEntry entry = this.findNodeEntry((float) event.mouseX, (float) event.mouseY);
        if (entry == null) {
            return;
        }
        entry.setDebugClicked(!entry.isDebugClicked());
        context.consume();
        context.stopPropagation();
    }

    @Override
    public void onDoubleClick(UIEventContext<DoubleClick> context) {
        this.onMouseClick(UIEventContext.upcast(AllUIEvents.MOUSE_CLICK, context));
    }

    @Override
    public void onMouseRelease(UIEventContext<MouseRelease> context) {
        if (context.isConsumed()) {
            return;
        }
        context.consume();
        context.stopPropagation();
    }

    @Nonnull
    public ParaTree getTree() {
        return this.tree;
    }

    private float getCanvasZoom() {
        UINode parent = this.getParent();
        if (parent instanceof CanvasNode canvas) {
            return canvas.zoom;
        }
        return 1.0f;
    }

    /**
     * 在给定屏幕空间鼠标坐标的情况下，寻找命中的节点.
     * @return 命中的 ParaNode，若没有命中任何节点则返回 null.
     */
    @Nullable
    public ParaNode findNode(float mouseX, float mouseY) {
        PTTreeLayout.NodeEntry entry = this.findNodeEntry(mouseX, mouseY);
        return entry == null ? null : entry.node;
    }

    @Nullable
    private PTTreeLayout.NodeEntry findNodeEntry(float mouseX, float mouseY) {
        float zoom = this.getCanvasZoom();
        float localX = (mouseX - this.worldRect.x) / zoom;
        float localY = (mouseY - this.worldRect.y) / zoom;
        float half = NODE_CELL_SIZE / 2.0f;
        for (PTTreeLayout.NodeEntry entry : this.layout.getNodeEntries()) {
            if (localX >= entry.x - half && localX <= entry.x + half
                    && localY >= entry.y - half && localY <= entry.y + half) {
                return entry;
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
        float zoom = this.getCanvasZoom();
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
            this.renderParaNodeEntry(
                    context,
                    entry,
                    entry.x, entry.y,
                    ox, oy,
                    zoom
            );
        }
    }

    /**
     * 在给定位置可视化渲染一个 ParaNodeEntry.
     * @param entry 需要渲染的 ParaNodeEntry.
     * @param nodeX entry 的中心点在屏幕空间的 X 坐标.
     * @param nodeY entry 的中心点在屏幕空间的 Y 坐标.
     */
    protected void renderParaNodeEntry(
            @Nonnull UIRenderContext context,
            @Nonnull PTTreeLayout.NodeEntry entry,
            float nodeX, float nodeY,
            float offsetX, float offsetY,
            float zoom
    ) {
        final float x = offsetX + nodeX * zoom;
        final float y = offsetY + nodeY * zoom;
        final float half = NODE_CELL_SIZE / 2.0f;
//        context.fill(x - half * zoom, y - half * zoom, x + half * zoom, y + half * zoom, UIColor.fromRGBA(255, 0, 0, 255));
        PoseStack view = RenderSystem.getModelViewStack();
        view.pushPose();
        view.translate(x, y, 0.0f);
        view.scale(zoom, zoom, 1.0f);
        RenderSystem.applyModelViewMatrix();
        context.fill(-half, -half, half, half, UIColor.fromRGBA(255, 0, 0, 255));

        context.renderItem(testItem, (int) (-half), (int) (-half));

        if (this.debugMod && entry.isDebugClicked()) {
            context.drawText("click", -half, -half, UIColor.GREEN);
        }

        view.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
