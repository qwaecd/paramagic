package com.qwaecd.paramagic.core.render.geometricmask;

import org.joml.Matrix4f;

/**
 * Mask pass 与 effect pass 共用的每帧/每物体上下文（投影、视图、相对模型矩阵、时间、mask 视口）。
 */
public class GeometricMaskUniformContext {
    public final Matrix4f projection = new Matrix4f();
    public final Matrix4f view = new Matrix4f();
    public final Matrix4f modelRelative = new Matrix4f();
    public float timeSeconds;
    public int maskFramebufferWidth = 1;
    public int maskFramebufferHeight = 1;
}
