package com.qwaecd.paramagic.data.animation.property;

import com.qwaecd.paramagic.client.animation.PropertyAccessor;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class AllAnimatableProperties {
    public static final AnimatableProperty<Quaternionf> ROTATION = new AnimatableProperty<>("rotation", Quaternionf.class, (transform, material) -> (PropertyAccessor<Quaternionf>) transform::setRotation);
    public static final AnimatableProperty<Vector3f> POSITION = new AnimatableProperty<>("position", Vector3f.class, (transform, material) -> (PropertyAccessor<Vector3f>) transform::setPosition);
    public static final AnimatableProperty<Vector3f> SCALE = new AnimatableProperty<>("scale", Vector3f.class, (transform, material) -> (PropertyAccessor<Vector3f>) transform::setScale);
    public static final AnimatableProperty<Vector4f> COLOR = new AnimatableProperty<>("color", Vector4f.class, (transform, material) -> (PropertyAccessor<Vector4f>) material.animationColor::set);
    public static final AnimatableProperty<Vector3f> EMISSIVE_COLOR = new AnimatableProperty<>("emissiveColor", Vector3f.class, (transform, material) -> (PropertyAccessor<Vector3f>) material::setEmissiveColor);
    public static final AnimatableProperty<Float> EMISSIVE_INTENSITY = new AnimatableProperty<>("emissiveIntensity", Float.class, (transform, material) -> (PropertyAccessor<Float>) material::setEmissiveIntensity);
}
