package com.qwaecd.paramagic.core.particle.data;


import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

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
 * </pre>
 */
@SuppressWarnings("unused")
@Getter
public final class EmissionRequest {
    @Setter
    private int count;
    private final int emitterType;
    private final int effectId;
    private int _padding;

    private Vector4f param1;
    private Vector4f param2;
    private Vector4f param3;
    private Vector4f param4;
    private Vector4f param5;

    public EmissionRequest(
            int count,
            int emitterType,
            int effectId,
            Vector4f param1,
            Vector4f param2,
            Vector4f param3,
            Vector4f param4,
            Vector4f param5
    ) {
        this.count = count;
        this.emitterType = emitterType;
        this.effectId = effectId;
        this._padding = 0;

        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.param5 = param5;
    }
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(count);
        buffer.putInt(emitterType);
        buffer.putInt(effectId);
        buffer.putInt(_padding);

        buffer.putFloat(param1.x);
        buffer.putFloat(param1.y);
        buffer.putFloat(param1.z);
        buffer.putFloat(param1.w);

        buffer.putFloat(param2.x);
        buffer.putFloat(param2.y);
        buffer.putFloat(param2.z);
        buffer.putFloat(param2.w);

        buffer.putFloat(param3.x);
        buffer.putFloat(param3.y);
        buffer.putFloat(param3.z);
        buffer.putFloat(param3.w);

        buffer.putFloat(param4.x);
        buffer.putFloat(param4.y);
        buffer.putFloat(param4.z);
        buffer.putFloat(param4.w);

        buffer.putFloat(param5.x);
        buffer.putFloat(param5.y);
        buffer.putFloat(param5.z);
        buffer.putFloat(param5.w);
    }

    public static final int SIZE_IN_BYTES = 96;
}
