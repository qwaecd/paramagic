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

public class LaserMaterial extends AbstractMaterial {
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

    public LaserMaterial() {
        this(DEFAULT_FLOW_TEXTURE, DEFAULT_NOISE_TEXTURE);
    }

    public LaserMaterial(ResourceLocation flowTextureLocation, ResourceLocation noiseTextureLocation) {
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

    public LaserMaterial setColor(float r, float g, float b) {
        this.color.set(r, g, b);
        return this;
    }

    public LaserMaterial setAlpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    public LaserMaterial setThreshold(float threshold) {
        this.threshold = threshold;
        return this;
    }

    public LaserMaterial setSoftness(float softness) {
        this.softness = softness;
        return this;
    }

    public LaserMaterial setEmissiveIntensity(float emissiveIntensity) {
        this.emissiveIntensity = emissiveIntensity;
        return this;
    }

    public LaserMaterial setUvScale(float u, float v) {
        this.uvScale.set(u, v);
        return this;
    }

    public LaserMaterial setFlowSpeed(float u, float v) {
        this.flowSpeed.set(u, v);
        return this;
    }

    public LaserMaterial setNoiseSpeed(float u, float v) {
        this.noiseSpeed.set(u, v);
        return this;
    }

    public LaserMaterial setNoiseScale(float noiseScale) {
        this.noiseScale = noiseScale;
        return this;
    }

    public LaserMaterial setNoiseStrength(float noiseStrength) {
        this.noiseStrength = noiseStrength;
        return this;
    }

}
