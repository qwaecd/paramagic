package com.qwaecd.paramagic.core.render.api;

import org.joml.Vector3f;

/**
 * Interface for materials that provide emissive lighting effect data.
 * Defines read-only access to emissive color and intensity properties.
 * <p>
 * 为支持发光效果的材质提供数据访问接口，定义对发光颜色和强度属性的只读访问
 */
public interface EmissiveProvider {
    /** Uniform name for emissive color in shaders - 着色器中发光颜色的uniform名称 */
    String EMISSIVE_COLOR_UNIFORM = "u_emissiveColor";
    /** Uniform name for emissive intensity in shaders - 着色器中发光强度的uniform名称 */
    String EMISSIVE_INTENSITY_UNIFORM = "u_emissiveIntensity";

    /**
     * Gets the emissive color of the material.
     * 获取材质的发光颜色
     *
     * @return RGB color vector representing emissive color - 表示发光颜色的RGB颜色向量
     */
    Vector3f getEmissiveColor();

    /**
     * Gets the emissive intensity of the material.
     * 获取材质的发光强度
     *
     * @return Emissive intensity value, typically > 1.0 for HDR emission - 发光强度值，通常 > 1.0 表示HDR发光
     */
    float getEmissiveIntensity();
}
