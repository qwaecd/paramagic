package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.core.UINode;

import java.util.List;

public class UIPanel extends UINode {
    protected final int expectItemRow;
    protected final int expectItemCol;
    protected final float maxLength;
    protected final boolean isMaxWidth;
    protected final int gapX;
    protected final int gapY;
    protected float cellSize;

    /**
     * 从期望的物品行列数和最大长度创建 UIPanel, 自动布局产生的元素长度不会超过指定的边
     * @param expectItemRow 期望的物品行数
     * @param expectItemCol 期望的物品列数
     * @param maxLength 限制边的长度
     * @param isMaxWidth 限制边长度 (maxLength) 是否是宽度
     */
    public UIPanel(int expectItemRow, int expectItemCol, float maxLength, boolean isMaxWidth, int itemSize) {
        this(expectItemRow, expectItemCol, maxLength, isMaxWidth, 1, 1, itemSize);
    }

    /**
     * 从期望的物品行列数和最大长度创建 UIPanel, 自动布局产生的元素长度不会超过指定的边
     * @param expectItemRow 期望的物品行数
     * @param expectItemCol 期望的物品列数
     * @param maxLength 限制边的长度
     * @param isLimitWidth 限制边长度 (maxLength) 是否是宽度
     * @param gapX 物品格在 X 方向的间隔, 单位是像素
     * @param gapY 物品格在 Y 方向的间隔, 单位是像素
     * @param itemSize 物品单元格的大小, 单元格是正方形, 不会小于 0
     */
    public UIPanel(int expectItemRow, int expectItemCol, float maxLength, boolean isLimitWidth, int gapX, int gapY, int itemSize) {
        this.gapX = Math.max(gapX, 0);
        this.gapY = Math.max(gapY, 0);
        this.expectItemRow = Math.max(expectItemRow, 1);
        this.expectItemCol = Math.max(expectItemCol, 1);
        this.maxLength = maxLength;
        this.isMaxWidth = isLimitWidth;
        this.cellSize = Math.max(itemSize, 0);
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        if (!this.children.isEmpty()) {
            this.updatePanelSize();
            this.layoutChildren();
        }
        super.layout(parentX, parentY, parentW, parentH);
    }

    private void updatePanelSize() {
        int itemCount = this.children.size();
        if (itemCount == 0) {
            this.localRect.setWH(0, 0);
            return;
        }

        if (this.isMaxWidth) {
            // 宽度受限，单元格不超过 maxLength 约束下的最大值
            float maxCellSize = (this.maxLength - (this.expectItemCol - 1) * this.gapX) / this.expectItemCol;
            this.cellSize = Math.min(this.cellSize, maxCellSize);

            int actualRows = (itemCount + this.expectItemCol - 1) / this.expectItemCol;
            float panelWidth = this.cellSize * this.expectItemCol + (this.expectItemCol - 1) * this.gapX;
            float panelHeight = this.cellSize * actualRows + (actualRows - 1) * this.gapY;
            this.localRect.setWH(panelWidth, panelHeight);
        } else {
            // 高度受限，单元格不超过 maxLength 约束下的最大值
            float maxCellSize = (this.maxLength - (this.expectItemRow - 1) * this.gapY) / this.expectItemRow;
            this.cellSize = Math.min(this.cellSize, maxCellSize);

            int actualCols = (itemCount + this.expectItemRow - 1) / this.expectItemRow;
            float panelWidth = this.cellSize * actualCols + (actualCols - 1) * this.gapX;
            float panelHeight = this.cellSize * this.expectItemRow + (this.expectItemRow - 1) * this.gapY;
            this.localRect.setWH(panelWidth, panelHeight);
        }
    }

    private void layoutChildren() {
        List<UINode> nodes = this.children;
        int count = nodes.size();
        float stepX = this.cellSize + this.gapX;
        float stepY = this.cellSize + this.gapY;

        for (int index = 0; index < count; index++) {
            UINode node = nodes.get(index);
            if (!(node instanceof ItemNode)) {
                continue;
            }

            int row, col;
            if (this.isMaxWidth) {
                row = index / this.expectItemCol;
                col = index % this.expectItemCol;
            } else {
                col = index / this.expectItemRow;
                row = index % this.expectItemRow;
            }

            node.localRect.setXY(col * stepX, row * stepY);
            node.localRect.setWH(this.cellSize, this.cellSize);
        }
    }
}
