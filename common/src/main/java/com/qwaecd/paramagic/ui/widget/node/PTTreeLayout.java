package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.thaumaturgy.node.ParaNode;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预计算的 ParaTree 布局数据，用于可视化渲染.
 * <p>
 * 层次居中式节点树布局：同深度节点在同一水平线，
 * 父节点水平居中于其子组之间，兄弟按输入顺序从左到右排列.
 */
public class PTTreeLayout {

    /**
     * 预计算的节点位置条目.
     */
    public static class NodeEntry {
        /** 对应的 ParaNode. */
        @Nonnull
        public final ParaNode node;
        /** 节点中心点的 x 坐标（相对于布局原点）. */
        public final float x;
        /** 节点中心点的 y 坐标（相对于布局原点）. */
        public final float y;

        private boolean debugClicked = false;

        public NodeEntry(@Nonnull ParaNode node, float x, float y) {
            this.node = node;
            this.x = x;
            this.y = y;
        }

        public void setDebugClicked(boolean clicked) {
            this.debugClicked = clicked;
        }

        public boolean isDebugClicked() {
            return this.debugClicked;
        }
    }

    /**
     * 预计算的连接线段（水平或竖直）.
     */
    public static class EdgeSegment {
        public final float x1, y1, x2, y2;

        public EdgeSegment(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    @Nonnull
    private final List<NodeEntry> nodeEntries;
    @Nonnull
    private final List<EdgeSegment> edgeSegments;

    public PTTreeLayout(@Nonnull ParaTree tree, int nodeSize, int hGap, int vGap) {
        List<NodeEntry> nodes = new ArrayList<>();
        List<EdgeSegment> edges = new ArrayList<>();

        ParaNode root = tree.getRootNode();

        //noinspection ConstantValue
        if (root != null) {
            Map<ParaNode, Float> widths = new HashMap<>();
            computeSubtreeWidths(root, nodeSize, hGap, widths);

            Map<ParaNode, float[]> positions = new HashMap<>();
            assignPositions(root, 0, 0, nodeSize, hGap, vGap, widths, positions);

            for (Map.Entry<ParaNode, float[]> entry : positions.entrySet()) {
                float[] pos = entry.getValue();
                nodes.add(new NodeEntry(entry.getKey(), pos[0], pos[1]));
            }

            buildEdges(root, nodeSize, positions, edges);
        }

        this.nodeEntries = List.copyOf(nodes);
        this.edgeSegments = List.copyOf(edges);
    }

    /**
     * 后序遍历计算每个节点子树所占的水平宽度.
     */
    private static float computeSubtreeWidths(
            @Nonnull ParaNode node, int nodeSize, int hGap, @Nonnull Map<ParaNode, Float> widths
    ) {
        List<ParaNode> children = node.getChildren();
        if (children.isEmpty()) {
            widths.put(node, (float) nodeSize);
            return nodeSize;
        }
        float totalWidth = 0;
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) totalWidth += hGap;
            totalWidth += computeSubtreeWidths(children.get(i), nodeSize, hGap, widths);
        }
        float width = Math.max(nodeSize, totalWidth);
        widths.put(node, width);
        return width;
    }

    /**
     * 前序遍历分配每个节点的 (x, y) 中心坐标.
     * <p>
     * y = depth * (nodeSize + vGap) + nodeSize / 2.
     * 子节点在父节点分配的水平区间内从左到右排列；父节点居中于第一与最后子节点之间.
     */
    private static void assignPositions(
            @Nonnull ParaNode node,
            float leftX,
            int depth,
            int nodeSize,
            int hGap,
            int vGap,
            @Nonnull Map<ParaNode, Float> widths,
            @Nonnull Map<ParaNode, float[]> positions
    ) {
        float y = depth * (nodeSize + vGap) + nodeSize / 2.0f;

        List<ParaNode> children = node.getChildren();
        if (children.isEmpty()) {
            positions.put(node, new float[]{leftX + nodeSize / 2.0f, y});
            return;
        }

        // 将子节点组居中于本节点分配的子树宽度内
        float childrenTotalWidth = 0;
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) childrenTotalWidth += hGap;
            childrenTotalWidth += widths.get(children.get(i));
        }
        float myWidth = widths.get(node);
        float currentX = leftX + (myWidth - childrenTotalWidth) / 2.0f;

        for (ParaNode child : children) {
            float childWidth = widths.get(child);
            assignPositions(child, currentX, depth + 1, nodeSize, hGap, vGap, widths, positions);
            currentX += childWidth + hGap;
        }

        // 父节点水平居中于第一与最后子节点
        float firstChildX = positions.get(children.get(0))[0];
        float lastChildX = positions.get(children.get(children.size() - 1))[0];
        positions.put(node, new float[]{(firstChildX + lastChildX) / 2.0f, y});
    }

    /**
     * 为每对父子节点构建三段式折线连接：
     * 父底部中心 → midY → 子顶部中心.
     */
    private static void buildEdges(
            @Nonnull ParaNode node,
            int nodeSize,
            @Nonnull Map<ParaNode, float[]> positions,
            @Nonnull List<EdgeSegment> edges
    ) {
        float[] parentPos = positions.get(node);
        if (parentPos == null) return;

        float halfSize = nodeSize / 2.0f;

        for (ParaNode child : node.getChildren()) {
            float[] childPos = positions.get(child);
            if (childPos == null) continue;

            float parentBottomY = parentPos[1] + halfSize;
            float childTopY = childPos[1] - halfSize;
            float midY = (parentBottomY + childTopY) / 2.0f;

            // 竖直：父底部中心 → midY
            edges.add(new EdgeSegment(parentPos[0], parentBottomY, parentPos[0], midY));
            // 水平：midY 处从 parentX 到 childX
            edges.add(new EdgeSegment(parentPos[0], midY, childPos[0], midY));
            // 竖直：midY → 子顶部中心
            edges.add(new EdgeSegment(childPos[0], midY, childPos[0], childTopY));

            buildEdges(child, nodeSize, positions, edges);
        }
    }

    @Nonnull
    public List<NodeEntry> getNodeEntries() {
        return this.nodeEntries;
    }

    @Nonnull
    public List<EdgeSegment> getEdgeSegments() {
        return this.edgeSegments;
    }
}
