package com.qwaecd.paramagic.core.render.vertex;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Mesh implements AutoCloseable{
    private final int vao;
    private final int vbo;
    private int ebo;
    private int indexCount;
    private int indexType;
    private int vertexCount;
    private final int drawMode;

    /**
     * @param drawMode The OpenGL draw mode (e.g., GL_TRIANGLES, GL_LINES).
     */
    public Mesh(int drawMode) {
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.ebo = 0;
        this.drawMode = drawMode;
    }

    public Mesh(DrawMode drawMode) {
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.ebo = 0;
        this.drawMode = drawMode.getGlMode();
    }

    /**
     * @param usage  OpenGL buffer usage hint (e.g., GL_STATIC_DRAW, GL_DYNAMIC_DRAW).
     */
    public void uploadAndConfigure(ByteBuffer buffer, VertexLayout layout, int usage) {
        if (layout == null || layout.getStride() == 0) {
            this.vertexCount = 0;
            this.indexCount = 0;
            return;
        }

        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);

        glBufferData(GL_ARRAY_BUFFER, buffer, usage);
        layout.apply();
        // buffer总字节数 / 每个顶点的总字节数(stride)
        this.vertexCount = buffer.remaining() / layout.getStride();
        this.indexCount = 0;

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * U16 indexes
     */
    public void uploadAndConfigure(ByteBuffer vertexBuffer,
                                   VertexLayout layout,
                                   int vertexUsage,
                                   ShortBuffer indexBuffer,
                                   int indexUsage) {
        uploadAndConfigureInternal(vertexBuffer, layout, vertexUsage, indexBuffer, GL_UNSIGNED_SHORT, indexUsage);
    }

    /**
     * U32 indexes
     */
    public void uploadAndConfigure(ByteBuffer vertexBuffer,
                                   VertexLayout layout,
                                   int vertexUsage,
                                   IntBuffer indexBuffer,
                                   int indexUsage) {
        uploadAndConfigureInternal(vertexBuffer, layout, vertexUsage, indexBuffer, GL_UNSIGNED_INT, indexUsage);
    }

    private void uploadAndConfigureInternal(ByteBuffer vertexBuffer,
                                            VertexLayout layout,
                                            int vertexUsage,
                                            Buffer typedIndexBuffer,
                                            int indexType,
                                            int indexUsage) {
        if (layout == null || layout.getStride() == 0) {
            this.vertexCount = 0;
            this.indexCount = 0;
            return;
        }

        glBindVertexArray(this.vao);

        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, vertexUsage);
        layout.apply();
        this.vertexCount = vertexBuffer.remaining() / layout.getStride();

        if (this.ebo == 0) {
            this.ebo = glGenBuffers();
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        if (indexType == GL_UNSIGNED_SHORT) {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, (ShortBuffer) typedIndexBuffer, indexUsage);
            this.indexCount = ((ShortBuffer) typedIndexBuffer).limit();
        } else {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) typedIndexBuffer, indexUsage);
            this.indexCount = ((IntBuffer) typedIndexBuffer).limit();
        }
        this.indexType = indexType;

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void draw() {
        if (indexCount == 0 && vertexCount == 0) return;
        glBindVertexArray(this.vao);
        if (this.ebo != 0 && this.indexCount > 0) {
            glDrawElements(this.drawMode, this.indexCount, this.indexType, 0L);
        } else {
            glDrawArrays(this.drawMode, 0, this.vertexCount);
        }
        glBindVertexArray(0);
    }

    /**
     * <ul>
     *   <li>Use POINTS for particle / debug markers. (点粒子 / 调试标记)</li>
     *   <li>Use LINES / LINE_STRIP for wireframes or outlines. (线框/轮廓)</li>
     *   <li>Use TRIANGLES for most solid geometry. (大多数实体几何)</li>
     *   <li>Use STRIP/FAN only when you manually build ordered vertices for reduced index count. (当可严格控制顶点顺序以减少索引时再用)</li>
     * </ul>
     */
    public enum DrawMode {
        /**
         * POINTS: Each vertex is rendered as an independent point. Size controlled by glPointSize.
         * 无连接：每个顶点独立成为一个点，可用 glPointSize 控制大小，常用于粒子/调试可视化。
         */
        POINTS(GL_POINTS),
        /**
         * LINES: Each pair of vertices (v0,v1), (v2,v3), ... forms an independent line segment.
         * 顶点两两成段： (v0,v1), (v2,v3)... 若顶点数为奇数最后一个被忽略。适合分离的边/辅助线。
         */
        LINES(GL_LINES),
        /**
         * LINE_STRIP: A continuous polyline v0-v1-v2-... with shared vertices between segments.
         * 连续折线：相邻线段共享端点，形成一条连续路径 (v0-v1-v2-...). 适合轨迹/轮廓绘制。
         */
        LINE_STRIP(GL_LINE_STRIP),
        /**
         * TRIANGLES: Every consecutive group of 3 vertices (v0,v1,v2), (v3,v4,v5)... is an independent triangle.
         * 每3个顶点组成一个独立三角形，最常用、拓扑灵活，易与索引缓存/法线/UV 配合。
         */
        TRIANGLES(GL_TRIANGLES),
        /**
         * TRIANGLE_STRIP: First triangle uses (v0,v1,v2); each new vertex vn (n>=3) forms a triangle with the two previous (vn-2,vn-1,vn), winding alternates.
         * 条带三角形：首个三角形 (v0,v1,v2)，之后每加入一个顶点构成 (v(n-2), v(n-1), v(n))；绕向交替需注意法线。减少重复顶点/索引。
         */
        TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
        /**
         * TRIANGLE_FAN: First vertex is a shared center; each pair (v(n-1),vn) with v0 forms a triangle (v0,v(n-1),vn).
         * 扇形三角形：v0 作为中心，后续顶点围成扇叶 (v0, v(n-1), vn)，适合圆盘/径向结构，顶点顺序需保持一致绕向。
         */
        TRIANGLE_FAN(GL_TRIANGLE_FAN);

        private final int glMode;

        DrawMode(int glMode) {
            this.glMode = glMode;
        }

        /**
         * @return Raw OpenGL constant for this primitive mode. (返回对应的 OpenGL 常量)
         */
        public int getGlMode() {
            return glMode;
        }
    }

    @Override
    public void close() throws Exception {
        if (ebo != 0) glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }
}
