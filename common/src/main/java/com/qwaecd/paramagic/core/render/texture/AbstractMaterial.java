package com.qwaecd.paramagic.core.render.texture;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.queue.SupportsRenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Optional;

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

    /**
     * 检查材质是否支持特定效果类型
     * @param effectClass 效果接口的Class对象
     * @return 如果材质支持该效果则返回对应的接口实例，否则返回空
     */
    public <T> Optional<T> getEffect(Class<T> effectClass) {
        if (effectClass.isInstance(this)) {
            return Optional.of(effectClass.cast(this));
        }
        return Optional.empty();
    }

    /**
     * 检查材质是否支持特定效果
     * @param effectClass 效果接口的Class对象
     * @return 如果支持返回true，否则返回false
     */
    public boolean hasEffect(Class<?> effectClass) {
        return effectClass.isInstance(this);
    }

    /**
     * 获取材质效果访问器，提供友好的效果操作API
     * @return MaterialEffects实例
     */
    public MaterialEffects effects() {
        return new MaterialEffects(this);
    }

    public abstract void applyCustomUniforms();
    @Override
    public abstract RenderType getRenderType();
}
