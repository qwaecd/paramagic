package com.qwaecd.paramagic.core.render.geometricmask;

import com.qwaecd.paramagic.core.render.shader.Shader;

/**
 * 几何遮罩效果：由 mask shader（mesh 光栅化写入 mask FBO）与 effect shader（全屏采样场景+mask）组成。
 */
public interface IGeometricMaskEffect {
    Shader getMaskShader();

    Shader getEffectShader();

    GeometricMaskInputPolicy getInputPolicy();

    GeometricMaskBlendPolicy getBlendPolicy();

    GeometricMaskRegionStrategy getRegionStrategy();

    /**
     * 在绘制 mask mesh 之前调用；manager 已绑定 mask shader。
     */
    void applyMaskShaderUniforms(Shader maskShader, GeometricMaskUniformContext ctx);

    /**
     * 在绘制全屏 effect 之前调用；manager 已在 GL_TEXTURE0/1 绑定场景与 mask 纹理。
     */
    void applyEffectShaderUniforms(Shader effectShader);
}
