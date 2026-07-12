package com.qwaecd.paramagic.core.render.shader;

/**
 * Controls how shader definitions are loaded.
 *
 * <p>Shaders in {@link #GPU_PARTICLES} form one runtime feature: the GPU
 * particle system requires every program in the group. They therefore load as
 * one transaction instead of allowing a partially usable particle pipeline.</p>
 */
public enum ShaderLoadGroup {
    REQUIRED,
    GPU_PARTICLES
}
