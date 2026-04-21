package com.qwaecd.paramagic.core.particle;

@SuppressWarnings("unused")
public final class ShaderBindingPoints {
    private ShaderBindingPoints() {}

    public static final int GLOBAL_DATA = 0; // 全局资源
    public static final int PARTICLE_DATA = 1;  // 全局资源
    public static final int DEAD_LIST = 2;  // 全局资源
    public static final int EFFECT_META_DATA = 3;

    public static final int REQUESTS = 4;
    public static final int EMISSION_TASKS = 5;
    public static final int EFFECT_PHYSICS_PARAMS = 6;

    // 按图元类型分桶（点 / 三角形 / 矩形）
    public static final int BUCKET_COUNTERS = 7;
    public static final int BUCKET_POINT_INDICES = 8;
    public static final int BUCKET_TRIANGLE_INDICES = 9;
    public static final int BUCKET_QUAD_INDICES = 10;
    public static final int BUCKET_DRAW_COMMANDS = 11;
}
