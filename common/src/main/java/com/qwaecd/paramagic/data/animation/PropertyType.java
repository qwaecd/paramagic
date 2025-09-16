package com.qwaecd.paramagic.data.animation;


import lombok.Getter;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PropertyType<T> {
    @Getter
    private final String name;
    @Getter
    private final Class<T> valueClass;

    private PropertyType(String name, Class<T> valueClass) {
        this.name = name;
        this.valueClass = valueClass;
    }

    public static final PropertyType<Quaternionf> ROTATION = new PropertyType<>("rotation", Quaternionf.class);
    public static final PropertyType<Vector3f> POSITION = new PropertyType<>("position", Vector3f.class);
    public static final PropertyType<Vector3f> SCALE = new PropertyType<>("scale", Vector3f.class);
    public static final PropertyType<Vector4f> COLOR = new PropertyType<>("color", Vector4f.class);
    public static final PropertyType<Vector3f> EMISSIVE_COLOR = new PropertyType<>("emissiveColor", Vector3f.class);
    public static final PropertyType<Float> EMISSIVE_INTENSITY = new PropertyType<>("emissiveIntensity", Float.class);
}
