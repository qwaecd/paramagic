package com.qwaecd.paramagic.core.particle.data;

import org.joml.Vector4f;

/**
 * <pre>
 * struct Particle {
 *   // (16 bytes)
 *   vec4 meta;     // x: effectId, y: primitiveType(bits in float), z: facingMode(bits in float), w: unused
 *   // 物理属性 (16 bytes)
 *   vec4 position;    // x, y, z, mass
 *   // 物理属性 (16 bytes)
 *   vec4 velocity;    // vx, vy, vz, normal.x (when facingMode=normal)
 *   // 生命周期与朝向属性 (16 bytes)
 *   vec4 attributes;  // x: age, y: lifetime, z: normal.y, w: normal.z
 *   // 渲染属性 (16 bytes)
 *   vec4 renderAttribs;  // x: size, y: angle, z: angular_velocity, w: bloom_intensity
 *   // 颜色 (16 bytes)
 *   vec4 color;
 * };
 * </pre>
 */
@SuppressWarnings("unused")
public final class GPUParticle {
    private Vector4f mata;
    private Vector4f position;
    private Vector4f velocity;
    private Vector4f attributes;
    private Vector4f renderAttribs;
    private Vector4f color;

    public static final int SIZE_IN_BYTES = 96;
}
