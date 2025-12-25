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

    public static final PropertyKey<Vector3f> POSITION = register("position", new Vector3f(), Vector3f.class);
    public static final PropertyKey<Vector3f> BASE_VELOCITY = register("base_velocity", new Vector3f(), Vector3f.class);
    public static final PropertyKey<Float> VELOCITY_SPREAD = register("velocity_spread", 180.0f, Float.class);
    public static final PropertyKey<Vector4f> COLOR = register("color", new Vector4f(1f), Vector4f.class);
    public static final PropertyKey<Vector2f> LIFE_TIME_RANGE = register("lifetime_range", new Vector2f(0.1f), Vector2f.class);
    public static final PropertyKey<Vector2f> SIZE_RANGE = register("size_range", new Vector2f(1.0f), Vector2f.class);
    public static final PropertyKey<Float> SPHERE_RADIUS = register("sphere_radius", 1.0f, Float.class);
    public static final PropertyKey<Float> BLOOM_INTENSITY = register("bloom_intensity", 0.0f, Float.class);

    public static final PropertyKey<Boolean> EMIT_FROM_VOLUME = register("emit_from_volume", true, Boolean.class);
    public static final PropertyKey<CubeAABB> CUBE_AABB = register("cube_aabb", new CubeAABB(), CubeAABB.class);
    public static final PropertyKey<VelocityModeStates> VELOCITY_MODE = register("velocity_mode", VelocityModeStates.RANDOM, VelocityModeStates.class);

    /**
     * For LineEmitter: The end position of the line segment.
     */
    public static final PropertyKey<Vector3f> END_POSITION = register("end_position", new Vector3f(1.0f), Vector3f.class);

    /**
     * For CircleEmitter: The inner and outer radius of the circle.
     */
    public static final PropertyKey<Vector2f> INNER_OUTER_RADIUS = register("inner_outer_radius", new Vector2f(0.5f, 1.0f), Vector2f.class);
    public static final PropertyKey<Vector3f> NORMAL = register("normal", new Vector3f(0.0f, 1.0f, 0.0f), Vector3f.class);

    public static <T> PropertyKey<T> register(String name, T defaultValue, Class<T> clazz) {
        PropertyKey<T> key = new PropertyKey<>(name, defaultValue, clazz);
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
