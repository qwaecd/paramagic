package com.qwaecd.paramagic.core.render.geometricmask;

import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 屏幕空间扭曲：mask 通道布局见 {@link GeometricMaskChannelLayout#DOC_DISTORTION_FIELD}，
 * 对应 shader {@code distortion_field} + {@code screen_warp}。
 */
@Accessors(chain = true)
public class DistortionGeometricMaskEffect implements IGeometricMaskEffect {
    private final Shader maskShader;
    private final Shader effectShader;

    @Getter
    @Setter
    private float projectedCenterU = 0.5f;
    @Getter
    @Setter
    private float projectedCenterV = 0.5f;
    @Getter
    @Setter
    private float distortionStrength = 0.01f;
    @Getter
    @Setter
    private float innerRadius = 0.02f;
    @Getter
    @Setter
    private float outerRadius = 0.18f;
    @Getter
    @Setter
    private float maxOffset = 0.04f;

    public DistortionGeometricMaskEffect() {
        this.maskShader = ShaderManager.getInstance().getShaderThrowIfNotFound("distortion_field");
        this.effectShader = ShaderManager.getInstance().getShaderThrowIfNotFound("screen_warp");
    }

    @Override
    public Shader getMaskShader() {
        return maskShader;
    }

    @Override
    public Shader getEffectShader() {
        return effectShader;
    }

    @Override
    public GeometricMaskInputPolicy getInputPolicy() {
        return GeometricMaskInputPolicy.POST_BLOOM_COMBINED;
    }

    @Override
    public GeometricMaskBlendPolicy getBlendPolicy() {
        return GeometricMaskBlendPolicy.OVERWRITE;
    }

    @Override
    public GeometricMaskRegionStrategy getRegionStrategy() {
        return GeometricMaskRegionStrategy.MASK_TEXTURE;
    }

    @Override
    public void applyMaskShaderUniforms(Shader shader, GeometricMaskUniformContext ctx) {
        shader.setUniformValue2f("u_centerUv", projectedCenterU, projectedCenterV);
        shader.setUniformValue2f("u_viewportSize", ctx.maskFramebufferWidth, ctx.maskFramebufferHeight);
        shader.setUniformValue1f("u_distortionStrength", distortionStrength);
        shader.setUniformValue1f("u_innerRadius", innerRadius);
        shader.setUniformValue1f("u_outerRadius", outerRadius);
        shader.setUniformValue1f("u_maxOffset", maxOffset);
    }

    @Override
    public void applyEffectShaderUniforms(Shader shader) {
        shader.setUniformValue1i("u_sceneTexture", 0);
        shader.setUniformValue1i("u_distortionFieldTexture", 1);
    }
}
