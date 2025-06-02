package com.qwaecd.paramagic.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MagicCircleRenderer {

    // Custom render states for magic circles
    private static final RenderStateShard.TransparencyStateShard MAGIC_TRANSPARENCY =
            new RenderStateShard.TransparencyStateShard("magic_transparency", () -> {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
            }, () -> {
                RenderSystem.disableBlend();
            });

    private static final RenderStateShard.LightmapStateShard MAGIC_LIGHTMAP =
            new RenderStateShard.LightmapStateShard(true);

    private static final RenderStateShard.OverlayStateShard MAGIC_OVERLAY =
            new RenderStateShard.OverlayStateShard(true);

    private static final RenderStateShard.CullStateShard MAGIC_CULL =
            new RenderStateShard.CullStateShard(false);

    private static final RenderStateShard.DepthTestStateShard MAGIC_DEPTH_TEST =
            new RenderStateShard.DepthTestStateShard("magic_depth_test", 515); // GL_LEQUAL

    private static final RenderStateShard.WriteMaskStateShard MAGIC_WRITE_MASK =
            new RenderStateShard.WriteMaskStateShard(true, false);

    // Custom RenderType for magic circles
    public static final RenderType MAGIC_CIRCLE_TYPE = createMagicCircleRenderType();

    private static RenderType createMagicCircleRenderType() {
        RenderStateShard.ShaderStateShard shaderState = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexShader);

        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(shaderState)
                .setTransparencyState(MAGIC_TRANSPARENCY)
                .setLightmapState(MAGIC_LIGHTMAP)
                .setOverlayState(MAGIC_OVERLAY)
                .setCullState(MAGIC_CULL)
                .setDepthTestState(MAGIC_DEPTH_TEST)
                .setWriteMaskState(MAGIC_WRITE_MASK)
                .createCompositeState(true);

        return RenderType.create(
                "paramagic_magic_circle",
                DefaultVertexFormat.POSITION_COLOR_TEX,
                VertexFormat.Mode.QUADS,
                256,
                true,
                false,
                state
        );
    }

    /**
     * Render a magic circle with the given parameters
     */
    public static void renderCircle(MagicCircle circle, PoseStack poseStack, VertexConsumer vertexConsumer) {
        float radius = circle.getCurrentRadius();
        float alpha = circle.getAlpha();

        if (radius <= 0 || alpha <= 0) {
            return;
        }

        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();

        // Render main circle
        renderCircleRing(matrix4f, matrix3f, vertexConsumer, radius, radius * 0.95f, 64, alpha);

        // Render inner decorative circles
        renderCircleRing(matrix4f, matrix3f, vertexConsumer, radius * 0.8f, radius * 0.75f, 32, alpha * 0.7f);
        renderCircleRing(matrix4f, matrix3f, vertexConsumer, radius * 0.6f, radius * 0.55f, 24, alpha * 0.5f);

        // Render center glow
        renderCircleFilled(matrix4f, matrix3f, vertexConsumer, radius * 0.3f, 16, alpha * 0.3f);

        // Render runic symbols (simplified as small circles for now)
        renderRunicSymbols(matrix4f, matrix3f, vertexConsumer, radius, alpha);
    }

    private static void renderCircleRing(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer,
                                         float outerRadius, float innerRadius, int segments, float alpha) {
        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (2 * Math.PI * i / segments);
            float angle2 = (float) (2 * Math.PI * (i + 1) / segments);

            float cos1 = (float) Math.cos(angle1);
            float sin1 = (float) Math.sin(angle1);
            float cos2 = (float) Math.cos(angle2);
            float sin2 = (float) Math.sin(angle2);

            // Outer edge
            float x1 = outerRadius * cos1;
            float z1 = outerRadius * sin1;
            float x2 = outerRadius * cos2;
            float z2 = outerRadius * sin2;

            // Inner edge
            float x3 = innerRadius * cos1;
            float z3 = innerRadius * sin1;
            float x4 = innerRadius * cos2;
            float z4 = innerRadius * sin2;

            // Create quad (two triangles)
            addVertex(vertexConsumer, matrix4f, matrix3f, x1, 0, z1, 0, 1, 0, alpha, 1, 0);
            addVertex(vertexConsumer, matrix4f, matrix3f, x3, 0, z3, 0, 1, 0, alpha, 0, 0);
            addVertex(vertexConsumer, matrix4f, matrix3f, x4, 0, z4, 0, 1, 0, alpha, 0, 1);
            addVertex(vertexConsumer, matrix4f, matrix3f, x2, 0, z2, 0, 1, 0, alpha, 1, 1);
        }
    }

    private static void renderCircleFilled(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer,
                                           float radius, int segments, float alpha) {
        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (2 * Math.PI * i / segments);
            float angle2 = (float) (2 * Math.PI * (i + 1) / segments);

            float cos1 = (float) Math.cos(angle1);
            float sin1 = (float) Math.sin(angle1);
            float cos2 = (float) Math.cos(angle2);
            float sin2 = (float) Math.sin(angle2);

            float x1 = radius * cos1;
            float z1 = radius * sin1;
            float x2 = radius * cos2;
            float z2 = radius * sin2;

            // Triangle from center
            addVertex(vertexConsumer, matrix4f, matrix3f, 0, 0, 0, 0, 1, 0, alpha, 0.5f, 0.5f);
            addVertex(vertexConsumer, matrix4f, matrix3f, x1, 0, z1, 0, 1, 0, alpha, 0, 0);
            addVertex(vertexConsumer, matrix4f, matrix3f, x2, 0, z2, 0, 1, 0, alpha, 1, 0);
            addVertex(vertexConsumer, matrix4f, matrix3f, 0, 0, 0, 0, 1, 0, alpha, 0.5f, 0.5f);
        }
    }

    private static void renderRunicSymbols(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer,
                                           float radius, float alpha) {
        int symbolCount = 8;
        float symbolRadius = radius * 0.9f;
        float symbolSize = radius * 0.05f;

        for (int i = 0; i < symbolCount; i++) {
            float angle = (float) (2 * Math.PI * i / symbolCount);
            float x = symbolRadius * (float) Math.cos(angle);
            float z = symbolRadius * (float) Math.sin(angle);

            // Render simple symbol as small filled circle
            renderSymbolAt(matrix4f, matrix3f, vertexConsumer, x, z, symbolSize, alpha * 0.8f);
        }
    }

    private static void renderSymbolAt(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer,
                                       float centerX, float centerZ, float size, float alpha) {
        int segments = 8;
        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (2 * Math.PI * i / segments);
            float angle2 = (float) (2 * Math.PI * (i + 1) / segments);

            float cos1 = (float) Math.cos(angle1);
            float sin1 = (float) Math.sin(angle1);
            float cos2 = (float) Math.cos(angle2);
            float sin2 = (float) Math.sin(angle2);

            float x1 = centerX + size * cos1;
            float z1 = centerZ + size * sin1;
            float x2 = centerX + size * cos2;
            float z2 = centerZ + size * sin2;

            // Triangle from center
            addVertex(vertexConsumer, matrix4f, matrix3f, centerX, 0, centerZ, 0, 1, 0, alpha, 0.5f, 0.5f);
            addVertex(vertexConsumer, matrix4f, matrix3f, x1, 0, z1, 0, 1, 0, alpha, 0, 0);
            addVertex(vertexConsumer, matrix4f, matrix3f, x2, 0, z2, 0, 1, 0, alpha, 1, 0);
            addVertex(vertexConsumer, matrix4f, matrix3f, centerX, 0, centerZ, 0, 1, 0, alpha, 0.5f, 0.5f);
        }
    }

    private static void addVertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f,
                                  float x, float y, float z, float nx, float ny, float nz,
                                  float alpha, float u, float v) {
        vertexConsumer.vertex(matrix4f, x, y, z)
                .color(1.0f, 1.0f, 1.0f, alpha)
                .uv(u, v)
                .overlayCoords(0, 10)
                .uv2(240, 240) // Full brightness
                .normal(matrix3f, nx, ny, nz)
                .endVertex();
    }
}