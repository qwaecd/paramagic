package com.qwaecd.paramagic.client.material;

import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.texture.Texture2D;
import com.qwaecd.paramagic.core.render.texture.TextureManager;
import com.qwaecd.paramagic.tools.ModRL;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL33.GL_REPEAT;

public class EnergyFlowMaterial extends AbstractMaterial {
    public static final ResourceLocation DEFAULT_FLOW_TEXTURE = ModRL.inModSpace("textures/special/fluent_light.png");
    public static final ResourceLocation DEFAULT_NOISE_TEXTURE = ModRL.inModSpace("textures/special/noise_0.png");

    private final Texture2D flowTexture;
    private final Texture2D noiseTexture;
    private final Vector3f color = new Vector3f(0.35f, 0.75f, 1.0f);
    private final Vector2f uvScale = new Vector2f(1.0f, 1.0f);
    private final Vector2f flowSpeed = new Vector2f(0.0f, -1.6f);
    private final Vector2f noiseSpeed = new Vector2f(0.15f, -0.35f);
    private float alpha = 1.0f;
    private float threshold = 0.35f;
    private float softness = 0.2f;
    private float emissiveIntensity = 2.5f;
    private float noiseScale = 2.0f;
    private float noiseStrength = 0.5f;

    public EnergyFlowMaterial() {
        this(DEFAULT_FLOW_TEXTURE, DEFAULT_NOISE_TEXTURE);
    }

    public EnergyFlowMaterial(ResourceLocation flowTextureLocation, ResourceLocation noiseTextureLocation) {
        super(ShaderManager.getInstance().getShaderThrowIfNotFound("laser_cylinder"));
        this.flowTexture = new Texture2D(flowTextureLocation, true, GL_REPEAT, GL_REPEAT);
        this.noiseTexture = new Texture2D(noiseTextureLocation, true, GL_REPEAT, GL_REPEAT);
    }

    @Override
    public void applyCustomUniforms() {
        TextureManager textures = ModRenderSystem.getInstance().getTextureManager();
        textures.bind(flowTexture, 0);
        textures.bind(noiseTexture, 1);
        shader.setUniformValue1i("u_flowTexture", 0);
        shader.setUniformValue1i("u_noiseTexture", 1);
        shader.setUniformValue3f("u_color", color);
        shader.setUniformValue1f("u_alpha", alpha);
        shader.setUniformValue1f("u_threshold", threshold);
        shader.setUniformValue1f("u_softness", softness);
        shader.setUniformValue1f("u_emissiveIntensity", emissiveIntensity);
        shader.setUniformValue2f("u_uvScale", uvScale.x, uvScale.y);
        shader.setUniformValue2f("u_flowSpeed", flowSpeed.x, flowSpeed.y);
        shader.setUniformValue2f("u_noiseSpeed", noiseSpeed.x, noiseSpeed.y);
        shader.setUniformValue1f("u_noiseScale", noiseScale);
        shader.setUniformValue1f("u_noiseStrength", noiseStrength);
    }

    @Override
    public RenderType getRenderType() {
        return RenderType.ADDITIVE;
    }

    /**
     * Sets the base tint multiplied into the visible energy pattern and bloom output.
     * <p>
     * 设置能量图案和 bloom 输出的基础染色。
     * <p>
     * Values above {@code 1.0} are allowed and make the additive effect brighter; visually this changes the
     * perceived energy color, bloom hue, and overall HDR intensity.
     * <p>
     * 允许使用大于 {@code 1.0} 的值，这会让加色效果更亮；视觉上会影响能量颜色、辉光颜色以及整体 HDR 亮度。
     *
     * @param r red multiplier / 红色通道倍率
     * @param g green multiplier / 绿色通道倍率
     * @param b blue multiplier / 蓝色通道倍率
     * @return this material for chaining / 当前材质实例，用于链式调用
     */
    public EnergyFlowMaterial setColor(float r, float g, float b) {
        this.color.set(r, g, b);
        return this;
    }

    /**
     * Sets the final opacity multiplier after the flow mask and noise mask are evaluated.
     * <p>
     * 设置 flow 遮罩和 noise 遮罩计算后的最终透明度倍率。
     * <p>
     * Lower values make the whole effect thinner and less intense; higher values make both the visible color
     * and bloom contribution stronger.
     * <p>
     * 较低的值会让整体效果更稀薄、更弱；较高的值会同时增强可见颜色和 bloom 贡献。
     *
     * @param alpha opacity multiplier, usually {@code 0.0..1.0} / 透明度倍率，通常使用 {@code 0.0..1.0}
     * @return this material for chaining / 当前材质实例，用于链式调用
     */
    public EnergyFlowMaterial setAlpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    /**
     * Sets the cutoff point used to extract bright regions from the flow texture.
     * <p>
     * 设置从 flow 贴图中提取亮部区域的截断阈值。
     * <p>
     * Higher values keep only brighter texture areas, making the pattern more broken and filament-like.
     * Lower values reveal more of the texture and make the surface look fuller or more solid.
     * <p>
     * 较高的值只保留更亮的贴图区域，让图案更破碎、更像能量丝线；较低的值会显示更多贴图内容，让表面更饱满、更接近实体。
     *
     * @param threshold flow texture cutoff / flow 贴图截断阈值
     * @return this material for chaining / 当前材质实例，用于链式调用
     */
    public EnergyFlowMaterial setThreshold(float threshold) {
        this.threshold = threshold;
        return this;
    }

    /**
     * Sets how gradually the flow texture fades in around {@link #setThreshold(float)}.
     * <p>
     * 设置 flow 贴图在 {@link #setThreshold(float)} 附近的渐变宽度。
     * <p>
     * Lower values create sharper, harder-edged streaks; higher values create softer transitions and a more
     * cloudy energy shell.
     * <p>
     * 较低的值会形成更锐利、边缘更硬的条纹；较高的值会让过渡更柔和，能量外壳更像云雾。
     *
     * @param softness transition width above the threshold / 阈值上方的过渡宽度
     * @return this material for chaining / 当前材质实例，用于链式调用
     */
    public EnergyFlowMaterial setSoftness(float softness) {
        this.softness = softness;
        return this;
    }

    /**
     * Sets the multiplier applied to the bloom output.
     * <p>
     * 设置输出到 bloom 通道的亮度倍率。
     * <p>
     * This mainly affects glow strength in post-processing. It does not directly change the alpha mask, but high
     * values can make the effect feel hotter, larger, and more overexposed.
     * <p>
     * 这个参数主要影响后处理辉光强度。它不会直接改变 alpha 遮罩，但较高的值会让效果看起来更炽热、更大、更过曝。
     *
     * @param emissiveIntensity bloom intensity multiplier / bloom 强度倍率
     * @return this material for chaining / 当前材质实例，用于链式调用
     */
    public EnergyFlowMaterial setEmissiveIntensity(float emissiveIntensity) {
        this.emissiveIntensity = emissiveIntensity;
        return this;
    }

    /**
     * Scales the mesh UVs before sampling both the flow texture and noise texture.
     * <p>
     * 在采样 flow 贴图和 noise 贴图前，对 mesh UV 进行缩放。
     * <p>
     * Higher values tile the pattern more often across the surface, producing smaller repeated details. Lower
     * values stretch the pattern, producing larger bands or blobs. {@code u} affects horizontal wrapping on the
     * current mesh UVs; {@code v} affects vertical/longitudinal wrapping.
     * <p>
     * 较高的值会让图案在表面重复更多次，产生更小、更密的细节；较低的值会拉伸图案，产生更大的带状或块状区域。
     * {@code u} 影响当前 mesh UV 的横向环绕，{@code v} 影响纵向或长度方向的环绕。
     *
     * @param u horizontal UV scale / 横向 UV 缩放
     * @param v vertical UV scale / 纵向 UV 缩放
     * @return this material for chaining / 当前材质实例，用于链式调用
     */
    public EnergyFlowMaterial setUvScale(float u, float v) {
        this.uvScale.set(u, v);
        return this;
    }

    /**
     * Sets the scrolling speed of the primary flow texture in UV space.
     * <p>
     * 设置主 flow 贴图在 UV 空间中的滚动速度。
     * <p>
     * This controls the main visible motion direction and speed of energy streaks. Positive or negative values
     * reverse movement along each UV axis; larger magnitudes make the flow move faster.
     * <p>
     * 这个参数控制能量条纹最主要的可见运动方向和速度。正负值会反转对应 UV 轴上的运动方向，绝对值越大流动越快。
     *
     * @param u horizontal flow speed in UV units per second / 横向 flow 速度，单位为每秒 UV 偏移
     * @param v vertical flow speed in UV units per second / 纵向 flow 速度，单位为每秒 UV 偏移
     * @return this material for chaining / 当前材质实例，用于链式调用
     */
    public EnergyFlowMaterial setFlowSpeed(float u, float v) {
        this.flowSpeed.set(u, v);
        return this;
    }

    /**
     * Sets the scrolling speed of the noise texture in UV space.
     * <p>
     * 设置 noise 贴图在 UV 空间中的滚动速度。
     * <p>
     * This controls how the breakup/modulation pattern moves over the primary flow. Different values from
     * {@link #setFlowSpeed(float, float)} create interference, boiling, or turbulent-looking motion.
     * <p>
     * 这个参数控制破碎和调制图案如何在主 flow 图案上移动。与 {@link #setFlowSpeed(float, float)} 使用不同的值，
     * 可以产生干涉、沸腾或湍流感的运动。
     *
     * @param u horizontal noise speed in UV units per second / 横向 noise 速度，单位为每秒 UV 偏移
     * @param v vertical noise speed in UV units per second / 纵向 noise 速度，单位为每秒 UV 偏移
     * @return this material for chaining / 当前材质实例，用于链式调用
     */
    public EnergyFlowMaterial setNoiseSpeed(float u, float v) {
        this.noiseSpeed.set(u, v);
        return this;
    }

    /**
     * Sets the additional tiling scale for the noise texture after the base UV scale is applied.
     * <p>
     * 设置基础 UV 缩放之后，noise 贴图额外使用的平铺缩放。
     * <p>
     * Higher values make the noise detail finer and more fragmented. Lower values make the modulation broader,
     * causing larger patches of the effect to pulse or fade together.
     * <p>
     * 较高的值会让噪声细节更细、更碎；较低的值会让调制范围更大，使更大片的效果一起闪烁或淡入淡出。
     *
     * @param noiseScale noise texture scale multiplier / noise 贴图缩放倍率
     * @return this material for chaining / 当前材质实例，用于链式调用
     */
    public EnergyFlowMaterial setNoiseScale(float noiseScale) {
        this.noiseScale = noiseScale;
        return this;
    }

    /**
     * Sets how strongly the noise texture modulates the primary flow mask.
     * <p>
     * 设置 noise 贴图对主 flow 遮罩的调制强度。
     * <p>
     * {@code 0.0} disables noise breakup and keeps the flow texture fully intact. {@code 1.0} applies the sampled
     * noise directly. Values outside that range are clamped by the shader, so visually the useful range is
     * {@code 0.0..1.0}.
     * <p>
     * {@code 0.0} 会关闭噪声破碎，让 flow 贴图保持完整；{@code 1.0} 会直接使用采样到的噪声进行调制。
     * 超出范围的值会在 shader 中被 clamp，因此视觉上有效范围是 {@code 0.0..1.0}。
     *
     * @param noiseStrength noise modulation strength / noise 调制强度
     * @return this material for chaining / 当前材质实例，用于链式调用
     */
    public EnergyFlowMaterial setNoiseStrength(float noiseStrength) {
        this.noiseStrength = noiseStrength;
        return this;
    }

}
