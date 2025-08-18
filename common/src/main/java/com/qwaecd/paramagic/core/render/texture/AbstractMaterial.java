package com.qwaecd.paramagic.core.render.texture;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.queue.SupportsRenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public abstract class AbstractMaterial implements SupportsRenderType {
    @Getter
    private final Shader shader;

    public final Vector4f baseColor;

    public AbstractMaterial(Shader shader) {
        this.shader = shader;
        this.baseColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public final void applyBaseUniforms(Matrix4f projection, Matrix4f view, Matrix4f model, float timeSeconds) {
        shader.bind();
        shader.setUniformMatrix4f("u_projection", projection);
        shader.setUniformMatrix4f("u_view", view);
        shader.setUniformMatrix4f("u_model", model);
        shader.setUniformValue1f("u_time", timeSeconds);
    }

    public final void applyUniforms() {
        shader.bind();
        applyCustomUniforms();
    }

    public void unbind() {
        shader.unbind();
    }

    public abstract void applyCustomUniforms();
    @Override
    public abstract RenderType getRenderType();
}
