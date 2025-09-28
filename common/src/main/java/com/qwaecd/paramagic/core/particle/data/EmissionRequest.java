package com.qwaecd.paramagic.core.particle.data;


import org.joml.Vector4f;

/**
 * <pre>
 * struct EmissionRequest {
 *     // --- 核心控制字段 ---
 *     int count;          // 本次请求发射的粒子数量
 *     int emitterType;    // 发射器类型ID (e.g., 0=POINT, 1=BURST_SPHERE)
 *     int effectId;       // (高级功能) 所属Effect的唯一ID，用于实现Effect级别的粒子上限
 *     int _padding;       // 填充，确保vec4对齐
 *
 *     // --- 通用参数 (其含义由emitterType解释) ---
 *     vec4 param1; // e.g., 发射源位置 (xyz)
 *     vec4 param2; // e.g., 基础速度或方向 (xyz)
 *     vec4 param3; // e.g., 颜色 (rgba)
 *     vec4 param4; // e.g., 粒子生命周期(min, max), 尺寸(min, max)
 *     vec4 param5; // e.g., (for BURST_SPHERE) 速度(min, max), (for POINT) 发射角度
 * };
 * <pre/>
 */
@SuppressWarnings("unused")
public final class EmissionRequest {
    private int count;
    private int emitterType;
    private int effectId;
    private int _padding;

    private Vector4f param1;
    private Vector4f param2;
    private Vector4f param3;
    private Vector4f param4;
    private Vector4f param5;

    public static final int SIZE_IN_BYTES = 96;
}
