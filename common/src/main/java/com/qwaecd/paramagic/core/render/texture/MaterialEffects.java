package com.qwaecd.paramagic.core.render.texture;

import com.qwaecd.paramagic.core.render.EmissiveProvider;
import com.qwaecd.paramagic.core.render.EmissiveMutable;
import org.joml.Vector3f;
import java.util.Optional;

/**
 * Material effect access utility class providing type-safe effect access methods.
 * Encapsulates common effect queries and operations to avoid repetitive type checking code.
 * <p>
 * 材质效果访问工具类，提供类型安全的效果访问方法，封装了常见效果的查询和操作，避免重复的类型检查代码
 */
public class MaterialEffects {
    private final AbstractMaterial material;

    public MaterialEffects(AbstractMaterial material) {
        this.material = material;
    }

    // ===== Emissive effect related methods - 发光效果相关方法 =====

    /**
     * Checks if the material supports emissive effect (read-only).
     * 检查材质是否支持发光效果（只读）
     */
    public boolean hasEmissive() {
        return material.hasEffect(EmissiveProvider.class);
    }

    /**
     * Checks if the material supports emissive effect modification.
     * 检查材质是否支持发光效果修改
     */
    public boolean isEmissiveMutable() {
        return material.hasEffect(EmissiveMutable.class);
    }

    /**
     * Gets the emissive effect provider (read-only access).
     * 获取发光效果提供者（只读访问）
     */
    public Optional<EmissiveProvider> getEmissiveProvider() {
        return material.getEffect(EmissiveProvider.class);
    }

    /**
     * Gets the emissive effect mutator (write access).
     * 获取发光效果修改器（可写访问）
     */
    public Optional<EmissiveMutable> getEmissiveMutable() {
        return material.getEffect(EmissiveMutable.class);
    }

    /**
     * Convenience method: Get emissive color.
     * 便利方法：获取发光颜色
     *
     * @return Emissive color, or null if emissive is not supported - 发光颜色，如果不支持发光则返回null
     */
    public Vector3f getEmissiveColor() {
        return getEmissiveProvider()
            .map(EmissiveProvider::getEmissiveColor)
            .orElse(null);
    }

    /**
     * Convenience method: Get emissive intensity.
     * 便利方法：获取发光强度
     *
     * @return Emissive intensity, or 0.0f if emissive is not supported - 发光强度，如果不支持发光则返回0.0f
     */
    public float getEmissiveIntensity() {
        return getEmissiveProvider()
            .map(EmissiveProvider::getEmissiveIntensity)
            .orElse(0.0f);
    }

    /**
     * Convenience method: Set emissive color (if modification is supported).
     * 便利方法：设置发光颜色（如果支持修改）
     *
     * @param r Red component - 红色分量
     * @param g Green component - 绿色分量
     * @param b Blue component - 蓝色分量
     * @return Whether the setting was successful - 是否设置成功
     */
    public boolean setEmissiveColor(float r, float g, float b) {
        Optional<EmissiveMutable> mutable = getEmissiveMutable();
        if (mutable.isPresent()) {
            mutable.get().setEmissiveColor(r, g, b);
            return true;
        }
        return false;
    }

    /**
     * Convenience method: Set emissive intensity (if modification is supported).
     * 便利方法：设置发光强度（如果支持修改）
     *
     * @param intensity Emissive intensity - 发光强度
     * @return Whether the setting was successful - 是否设置成功
     */
    public boolean setEmissiveIntensity(float intensity) {
        Optional<EmissiveMutable> mutable = getEmissiveMutable();
        if (mutable.isPresent()) {
            mutable.get().setEmissiveIntensity(intensity);
            return true;
        }
        return false;
    }

    /**
     * Fluent API support: Set emissive color.
     * 链式调用支持：设置发光���色
     */
    public MaterialEffects withEmissiveColor(float r, float g, float b) {
        setEmissiveColor(r, g, b);
        return this;
    }

    /**
     * Fluent API support: Set emissive intensity.
     * 链式调用支持：设置发光强度
     */
    public MaterialEffects withEmissiveIntensity(float intensity) {
        setEmissiveIntensity(intensity);
        return this;
    }
}
