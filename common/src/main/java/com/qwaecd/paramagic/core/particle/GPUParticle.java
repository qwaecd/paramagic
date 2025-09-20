package com.qwaecd.paramagic.core.particle;

import org.joml.Vector3f;
import org.joml.Vector4f;

public final class GPUParticle {
    public Vector3f position;
    public Vector3f velocity;

    public float age;
    public float lifetime;

    public Vector4f color;
    public float intensity;
    public float size;
    public float angle;
    public float angularVelocity;

    public int type;

    public static final int SIZE_IN_BYTES = 68; // 3*4 + 3*4 + 4 + 4 + 4*4 + 4*4 + 4 = 68 bytes
}
