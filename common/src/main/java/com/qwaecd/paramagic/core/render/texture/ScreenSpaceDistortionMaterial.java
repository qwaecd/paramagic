package com.qwaecd.paramagic.core.render.texture;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;

public class ScreenSpaceDistortionMaterial extends AbstractMaterial {
    private float projectedCenterU = 0.5f;
    private float projectedCenterV = 0.5f;
    private float viewportWidth = 1.0f;
    private float viewportHeight = 1.0f;

    private float distortionStrength = 0.01f;
    private float innerRadius = 0.02f;
    private float outerRadius = 0.18f;
    private float maxOffset = 0.04f;

    public ScreenSpaceDistortionMaterial() {
        this(ShaderManager.getInstance().getShaderThrowIfNotFound("distortion_field"));
    }

    public ScreenSpaceDistortionMaterial(Shader shader) {
        super(shader);
    }

    public ScreenSpaceDistortionMaterial setProjectedCenterUv(float u, float v) {
        this.projectedCenterU = u;
        this.projectedCenterV = v;
        return this;
    }

    public ScreenSpaceDistortionMaterial setViewportSize(float width, float height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        return this;
    }

    public ScreenSpaceDistortionMaterial setDistortionStrength(float distortionStrength) {
        this.distortionStrength = distortionStrength;
        return this;
    }

    public ScreenSpaceDistortionMaterial setInnerRadius(float innerRadius) {
        this.innerRadius = innerRadius;
        return this;
    }

    public ScreenSpaceDistortionMaterial setOuterRadius(float outerRadius) {
        this.outerRadius = outerRadius;
        return this;
    }

    public ScreenSpaceDistortionMaterial setMaxOffset(float maxOffset) {
        this.maxOffset = maxOffset;
        return this;
    }

    @Override
    public void applyCustomUniforms() {
        shader.setUniformValue2f("u_centerUv", projectedCenterU, projectedCenterV);
        shader.setUniformValue2f("u_viewportSize", viewportWidth, viewportHeight);
        shader.setUniformValue1f("u_distortionStrength", distortionStrength);
        shader.setUniformValue1f("u_innerRadius", innerRadius);
        shader.setUniformValue1f("u_outerRadius", outerRadius);
        shader.setUniformValue1f("u_maxOffset", maxOffset);
    }

    @Override
    public RenderType getRenderType() {
        return RenderType.DISTORTION;
    }
}
