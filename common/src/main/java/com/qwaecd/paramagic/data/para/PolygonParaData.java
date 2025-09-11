package com.qwaecd.paramagic.data.para;

public class PolygonParaData extends ParaComponentData {
    /**
     * The radius of the circumscribed circle of the polygon.
     * <p>
     * 多边形外接圆半径
     */
    public final float radius;
    public final int sides;
    /**
     * The starting angle in radians for the first vertex of the polygon.
     * 0 means the first vertex is on the positive X-axis.
     * PI/2 means it's on the positive Z-axis (in a right-handed Y-up system).
     */
    public final float startAngle;
    /**
     * The width of the line used to draw the polygon, measured in in-game blocks.
     * <p>
     * This value defines the radial distance between the inner and outer vertices that form the line.
     * Specifically, {@code outerRadius = radius + lineWidth / 2} and {@code innerRadius = radius - lineWidth / 2}.
     * <p>
     * <strong>Important:</strong> For non-circular polygons (where {@code sides} is low), the visual thickness of the
     * line will not be uniform. It will be exactly equal to {@code lineWidth} at the vertices, but will appear
     * narrower along the flat edges.
     * <p>
     * For example, in a triangle ({@code sides = 3}), the thickness at the middle of an edge will be
     * {@code lineWidth * cos(PI / 3)} or {@code lineWidth * 0.5}. To achieve an edge thickness of 1.0 block,
     * you would need to set {@code lineWidth} to 2.0.
     * <hr>
     * 用于绘制多边形的线条宽度，单位为游戏内的方块。
     * <p>
     * 该值定义了构成线条的内外顶点之间的径向距离。
     * 具体来说，{@code 外圈半径 = 中心半径 + 线宽 / 2}，{@code 内圈半径 = 中心半径 - 线宽 / 2}。
     * <p>
     * <strong>重要提示：</strong> 对于非圆形的多边形（即 {@code sides} 边数较少时），线条的视觉厚度将不是恒定的。
     * 它在顶点处的宽度精确等于 {@code lineWidth}，但在平直的边上会显得更窄。
     * <p>
     * 例如，在一个三角形（{@code sides = 3}）中，其边中点的厚度将是 {@code lineWidth * cos(PI / 3)}，即 {@code lineWidth * 0.5}。
     * 如果想要实现 1.0 个方块的边厚度，你需要将 {@code lineWidth} 设置为 2.0。
     */
    public final float lineWidth;
    public PolygonParaData(float radius, int sides, float startAngle, float lineWidth) {
        super();
        this.radius = radius;
        this.sides = Math.max(sides, 3);
        this.startAngle = startAngle;
        this.lineWidth = lineWidth;
    }

    public PolygonParaData(float radius, int sides) {
        super();
        this.radius = radius;
        this.sides = Math.max(sides, 3);
        this.startAngle = 0.0f;
        this.lineWidth = 4.0f;
    }
}
