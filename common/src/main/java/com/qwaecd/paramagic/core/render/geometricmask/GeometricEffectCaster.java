package com.qwaecd.paramagic.core.render.geometricmask;

import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import lombok.Getter;
import org.joml.Matrix4f;

import java.util.Optional;

/**
 * 几何遮罩发射体：用 mesh + 变换定义屏幕空间作用域，由 {@link IGeometricMaskEffect} 定义 mask/effect shader。
 * 与普通 {@link com.qwaecd.paramagic.core.render.api.IRenderable} 分离，不进入 {@link com.qwaecd.paramagic.core.render.queue.RenderType} 队列。
 */
public class GeometricEffectCaster {
    @Getter
    private final Mesh mesh;
    @Getter
    private final Transform transform;
    @Getter
    private final IGeometricMaskEffect effect;
    private Optional<Matrix4f> precomputedWorldTransform = Optional.empty();

    public GeometricEffectCaster(Mesh mesh, Transform transform, IGeometricMaskEffect effect) {
        this.mesh = mesh;
        this.transform = transform;
        this.effect = effect;
    }

    public Optional<Matrix4f> getPrecomputedWorldTransform() {
        return precomputedWorldTransform;
    }

    public GeometricEffectCaster setPrecomputedWorldTransform(Matrix4f worldMatrix) {
        this.precomputedWorldTransform = Optional.of(worldMatrix);
        return this;
    }
}
