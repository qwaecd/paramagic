package com.qwaecd.paramagic.core.render.texture;

import com.qwaecd.paramagic.core.render.shader.Shader;
import lombok.Getter;
import org.joml.Vector4f;

public class Material {
    @Getter
    private final Shader shader;

    // 材质可以有自己的参数
    public final Vector4f baseColor;
    // public final Map<Integer, Texture> textures;

    public Material(Shader shader) {
        this.shader = shader;
        this.baseColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        // this.textures = new HashMap<>();
    }

    public void apply() {
        shader.bind();

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

}
