package com.qwaecd.paramagic.core.particle.emitter.prop;

import com.qwaecd.paramagic.core.particle.emitter.impl.VelocityModeStates;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class AllEmitterProperties {
    private AllEmitterProperties() {}

    public static final PropertyKey<Vector3f> POSITION = new PropertyKey<>("position", new Vector3f());
    public static final PropertyKey<Vector3f> BASE_VELOCITY = new PropertyKey<>("base_velocity", new Vector3f());
    public static final PropertyKey<Float> VELOCITY_SPREAD = new PropertyKey<>("velocity_spread", 180.0f);
    public static final PropertyKey<Vector4f> COLOR = new PropertyKey<>("color", new Vector4f(1f));
    public static final PropertyKey<Vector2f> LIFE_TIME_RANGE = new PropertyKey<>("lifetime_range", new Vector2f(0.1f));
    public static final PropertyKey<Vector2f> SIZE_RANGE = new PropertyKey<>("size_range", new Vector2f(1.0f));
    public static final PropertyKey<Float> SPHERE_RADIUS = new PropertyKey<>("sphere_radius", 1.0f);
    public static final PropertyKey<Float> BLOOM_INTENSITY = new PropertyKey<>("bloom_intensity", 0.0f);

    public static final PropertyKey<Boolean> EMIT_FROM_VOLUME = new PropertyKey<>("emit_from_volume", true);
    public static final PropertyKey<CubeAABB> CUBE_AABB = new PropertyKey<>("cube_aabb", new CubeAABB());
    public static final PropertyKey<VelocityModeStates> VELOCITY_MODE = new PropertyKey<>("velocity_mode", VelocityModeStates.RANDOM);

    /**
     * For LineEmitter: The end position of the line segment.
     */
    public static final PropertyKey<Vector3f> END_POSITION = new PropertyKey<>("end_position", new Vector3f(1.0f));
}
