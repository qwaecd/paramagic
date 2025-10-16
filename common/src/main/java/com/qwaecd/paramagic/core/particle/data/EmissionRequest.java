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
 *     vec4 param6;
 * };
 * </pre>
 */
@SuppressWarnings({"unused", "FieldMayBeFinal"})
@Getter
public final class EmissionRequest {
    @Setter
    private int count;
    private final int emitterType;
    @Setter
    private int effectId;
    private int _padding;

    private Vector4f param1;
    private Vector4f param2;
    private Vector4f param3;
    private Vector4f param4;
    private Vector4f param5;
    private Vector4f param6;

    public EmissionRequest(
            int count,
            int emitterType,
            int effectId,
            Vector4f param1,
            Vector4f param2,
            Vector4f param3,
            Vector4f param4,
            Vector4f param5,
            Vector4f param6
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
        this.param6 = param6;
    }
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(count);
        buffer.putInt(emitterType);
        buffer.putInt(effectId);
        buffer.putInt(_padding);

        write(param1, buffer);
        write(param2, buffer);
        write(param3, buffer);
        write(param4, buffer);
        write(param5, buffer);
        write(param6, buffer);
    }

    private void write(Vector4f v, ByteBuffer buffer) {
        buffer.putFloat(v.x).putFloat(v.y).putFloat(v.z).putFloat(v.w);
    }

    private static final int paramCount = 6;

    public static final int SIZE_IN_BYTES = (4 * Integer.BYTES) + (paramCount * Float.BYTES * 4);
}
