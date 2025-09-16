package com.qwaecd.paramagic.data.animation;


import com.qwaecd.paramagic.client.animation.AccessorFactory;
import com.qwaecd.paramagic.client.animation.PropertyAccessor;
import lombok.Getter;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PropertyType<T> {
    @Getter
    private final String name;
    @Getter
    private final Class<T> valueClass;
    @Getter
    private final AccessorFactory<T> accessorFactory;

    private PropertyType(String name, Class<T> valueClass, AccessorFactory<T> accessorFactory) {
        this.name = name;
        this.valueClass = valueClass;
        this.accessorFactory = accessorFactory;
    }

    public static final PropertyType<Quaternionf> ROTATION = new PropertyType<>("rotation", Quaternionf.class, (transform, material) -> (PropertyAccessor<Quaternionf>) transform::setRotation);
    public static final PropertyType<Vector3f> POSITION = new PropertyType<>("position", Vector3f.class, (transform, material) -> (PropertyAccessor<Vector3f>) transform::setPosition);
    public static final PropertyType<Vector3f> SCALE = new PropertyType<>("scale", Vector3f.class, (transform, material) -> (PropertyAccessor<Vector3f>) transform::setScale);
    public static final PropertyType<Vector4f> COLOR = new PropertyType<>("color", Vector4f.class, (transform, material) -> (PropertyAccessor<Vector4f>) material.animationColor::set);
    public static final PropertyType<Vector3f> EMISSIVE_COLOR = new PropertyType<>("emissiveColor", Vector3f.class, (transform, material) -> (PropertyAccessor<Vector3f>) material::setEmissiveColor);
    public static final PropertyType<Float> EMISSIVE_INTENSITY = new PropertyType<>("emissiveIntensity", Float.class, (transform, material) -> (PropertyAccessor<Float>) material::setEmissiveIntensity);
}
