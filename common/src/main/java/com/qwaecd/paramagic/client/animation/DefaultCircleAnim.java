package com.qwaecd.paramagic.client.animation;

import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class DefaultCircleAnim extends Animator {
    private final IRenderable renderable;

    private boolean fullScale = false;

    private Vector3f targetScale;
    private float scaleDuration;
    private final Vector3f initialScale = new Vector3f();

    private float radianPerSecond = 1.0f;

    private final TempValue tempValue = new TempValue();

    public DefaultCircleAnim(@Nonnull IRenderable renderable) {
        this(renderable, new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
    }

    public DefaultCircleAnim(@Nonnull IRenderable renderable, Vector3f targetScale, float scaleDuration) {
        this.renderable = renderable;
        this.targetScale = targetScale;
        renderable.getTransform().getScale(this.initialScale);
        this.scaleDuration = scaleDuration;
    }

    public DefaultCircleAnim setTargetScale(Vector3f targetScale) {
        this.targetScale = targetScale;
        return this;
    }

    public DefaultCircleAnim setScaleDuration(float scaleDuration) {
        this.scaleDuration = scaleDuration;
        return this;
    }

    public DefaultCircleAnim setInitialScale(Vector3f initialScale) {
        this.initialScale.set(initialScale);
        return this;
    }

    public DefaultCircleAnim setRotationSpeed(float radianPerSecond) {
        this.radianPerSecond = radianPerSecond;
        return this;
    }

    @Override
    public void update(float deltaTime) {
        if (!isPlaying) {
            return;
        }

        this.currentTime += deltaTime * speed;
        Transform transform = renderable.getTransform();
        if (!this.fullScale) {
            float lerpX = Interpolation.lerp(this.initialScale.x, this.targetScale.x, Math.min(1.0f, this.currentTime / this.scaleDuration));
            float lerpY = Interpolation.lerp(this.initialScale.y, this.targetScale.y, Math.min(1.0f, this.currentTime / this.scaleDuration));
            float lerpZ = Interpolation.lerp(this.initialScale.z, this.targetScale.z, Math.min(1.0f, this.currentTime / this.scaleDuration));
            transform.setScale(lerpX, lerpY, lerpZ);
            if (this.currentTime >= this.scaleDuration) {
                this.fullScale = true;
            }
        }
        Quaternionf rotation = transform.getRotation(this.tempValue.quaternionf);
        rotation.rotateLocalY(this.radianPerSecond * deltaTime * speed);
        transform.setRotation(rotation);
    }

    @Override
    public List<AnimationTrack> getTracks() {
        return List.of();
    }

    @Override
    public void addTrack(AnimationTrack track) {
    }

    @Override
    public void reset() {
        super.reset();
        this.fullScale = false;
    }

    private static final class TempValue {
        final Vector3f vector = new Vector3f();
        final Quaternionf quaternionf = new Quaternionf();
    }
}
