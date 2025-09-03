package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.VertexAttribute;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30C.GL_FLOAT;

public class MagicCircleFactory {

    // 在 XY 平面上生成一个 2x2 的四边形（中心在原点），UV [0,1]
    public static TestObj create(ResourceLocation maskTexture) {
        // 顶点: pos(x,y,z), uv(u,v)
        float[] verts = new float[] {
                // tri 1
                -1f, -1f, 0f,  0f, 0f,
                1f, -1f, 0f,  1f, 0f,
                1f,  1f, 0f,  1f, 1f,
                // tri 2
                -1f, -1f, 0f,  0f, 0f,
                1f,  1f, 0f,  1f, 1f,
                -1f,  1f, 0f,  0f, 1f,
        };

        ByteBuffer buf = BufferUtils.createByteBuffer(verts.length * Float.BYTES);
        for (float v : verts) buf.putFloat(v);
        buf.flip();

        VertexLayout layout = new VertexLayout();
        layout.addAttribute(new VertexAttribute(0, 3, GL_FLOAT, false)); // aPos
        layout.addAttribute(new VertexAttribute(1, 2, GL_FLOAT, false)); // aUV

        Mesh mesh = new Mesh(GL_TRIANGLES);
        mesh.uploadAndConfigure(buf, layout, GL_STATIC_DRAW);

        AbstractMaterial mat = new MagicCircleMaterial(ShaderManager.getInstance().getMagicCircleShader(), maskTexture)
                .setIntensity(1.2f)
                .setPulse(0.8f, 0.2f)
                .setSweep(0.2f, 0.06f)
                .setRotationSpeed(1.0f);

        TestObj obj = new TestObj(mesh, mat);
        obj.getTransform().setPosition(0, 0, 0);
        return obj;
    }
}
