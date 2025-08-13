package com.qwaecd.paramagic.core.render.texture;

import com.qwaecd.paramagic.core.render.shader.Shader;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public abstract class AbstractMaterial implements UniformHandler {
    @Getter
    private final Shader shader;

    // 材质可以有自己的参数
    public final Vector4f baseColor;
    // public final Map<Integer, Texture> textures;

    public AbstractMaterial(Shader shader) {
        this.shader = shader;
        this.baseColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        // this.textures = new HashMap<>();
    }

    public final void applyBaseUniforms(Matrix4f projection, Matrix4f view, Matrix4f model, float timeSeconds) {
        shader.bind();
        shader.setUniformMatrix4f("u_projection", projection);
        shader.setUniformMatrix4f("u_view", view);
        shader.setUniformMatrix4f("u_model", model);
        shader.setUniformValue1f("u_time", timeSeconds);
    }

    public final void apply() {
        shader.bind();
        applyCustomUniforms();
        // 设置此材质的通用Uniforms
        // shader.uniformVec4f("u_MaterialColor", baseColor);

        // --- 纹理绑定逻辑将在这里 ---
        // for (Map.Entry<Integer, Texture> entry : textures.entrySet()) {
        //     int textureUnit = entry.getKey();
        //     Texture texture = entry.getValue();
        //     texture.bind(textureUnit);
        //     shader.uniform1i("u_TextureSampler" + textureUnit, textureUnit);
        // }
    }

    public void unbind() {
        // --- 解绑纹理 ---
        // for (int textureUnit : textures.keySet()) {
        //     Texture.unbind(textureUnit);
        // }
        shader.unbind();
    }

    @Override
    public abstract void applyCustomUniforms();
}
