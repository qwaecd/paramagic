package com.qwaecd.paramagic.core.particle;

@SuppressWarnings("unused")
public final class ShaderBindingPoints {
    private ShaderBindingPoints() {}

    public static final int GLOBAL_DATA = 0; // 全局资源
    public static final int PARTICLE_DATA = 1;  // 全局资源
    public static final int DEAD_LIST = 2;  // 全局资源
    public static final int EFFECT_COUNTERS = 3;

    public static final int REQUESTS = 4;
    public static final int EMISSION_TASKS = 5;
    public static final int EFFECT_PHYSICS_PARAMS = 6;
}
