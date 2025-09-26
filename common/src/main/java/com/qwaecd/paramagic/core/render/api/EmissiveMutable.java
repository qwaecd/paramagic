package com.qwaecd.paramagic.core.render.api;

/**
 * Interface for materials that support modifying emissive effect parameters.
 * Used together with EmissiveProvider to provide complete access to emissive effects.
 * <p>
 * 表示材质支持修改发光效果参数的能力，与 EmissiveProvider 配合使用，提供发光效果的完整访问接口
 */
public interface EmissiveMutable {
    /**
     * Sets the emissive color.
     * 设置发光颜色
     *
     * @param r Red component (0.0-1.0) - 红色分量 (0.0-1.0)
     * @param g Green component (0.0-1.0) - 绿色分量 (0.0-1.0)
     * @param b Blue component (0.0-1.0) - 蓝色分量 (0.0-1.0)
     */
    void setEmissiveColor(float r, float g, float b);

    /**
     * Sets the emissive intensity.
     * 设置发光强度
     *
     * @param intensity Emissive intensity, typically > 1.0 for HDR emission - 发光强度，通常 > 1.0 表示HDR发光
     */
    void setEmissiveIntensity(float intensity);
}
