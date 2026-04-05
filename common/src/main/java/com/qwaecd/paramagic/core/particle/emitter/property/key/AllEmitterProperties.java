package com.qwaecd.paramagic.core.particle.emitter.property.key;

import com.qwaecd.paramagic.core.particle.emitter.property.type.CubeAABB;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AllEmitterProperties {
    private static final Map<String, PropertyKey<?>> PROPERTY_KEY_MAP = new ConcurrentHashMap<>();
    private AllEmitterProperties() {}

    public static final PropertyKey<Vector3f> POSITION = register("position", Vector3f.class);
    public static final PropertyKey<Vector3f> BASE_VELOCITY = register("base_velocity", Vector3f.class);
    /**
     * 速度分布参数，通常情况下，这是以 度 为单位的，而不是弧度
     */
    public static final PropertyKey<Float> VELOCITY_SPREAD = register("velocity_spread", Float.class);
    /**
     * 粒子的颜色，是 HDR 颜色，允许分量的值大于1.0f
     */
    public static final PropertyKey<Vector4f> COLOR = register("color", Vector4f.class);
    public static final PropertyKey<Vector2f> LIFE_TIME_RANGE = register("lifetime_range", Vector2f.class);
    public static final PropertyKey<Vector2f> SIZE_RANGE = register("size_range", Vector2f.class);
    public static final PropertyKey<Float> SPHERE_RADIUS = register("sphere_radius", Float.class);
    public static final PropertyKey<Float> BLOOM_INTENSITY = register("bloom_intensity", Float.class);

    public static final PropertyKey<Boolean> EMIT_FROM_VOLUME = register("emit_from_volume", Boolean.class);
    public static final PropertyKey<CubeAABB> CUBE_AABB = register("cube_aabb", CubeAABB.class);
    public static final PropertyKey<VelocityModeStates> VELOCITY_MODE = register("velocity_mode", VelocityModeStates.class);

    /**
     * For LineEmitter: The end position of the line segment.
     */
    public static final PropertyKey<Vector3f> END_POSITION = register("end_position", Vector3f.class);

    /**
     * For CircleEmitter: The inner and outer radius of the circle.
     */
    public static final PropertyKey<Vector2f> INNER_OUTER_RADIUS = register("inner_outer_radius", Vector2f.class);
    public static final PropertyKey<Vector3f> NORMAL = register("normal", Vector3f.class);

    public static <T> PropertyKey<T> register(String name, Class<T> clazz) {
        PropertyKey<T> key = new PropertyKey<>(name, clazz);
        if (PROPERTY_KEY_MAP.putIfAbsent(name, key) != null) {
            throw new IllegalStateException("PropertyKey with name " + name + " is already registered.");
        }
        return key;
    }

    public static void registerAll() {
        // auto register all static fields
    }

    public static PropertyKey<?> get(String name) {
        return PROPERTY_KEY_MAP.get(name);
    }
}
