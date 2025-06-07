package com.qwaecd.paramagic.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix3f;

public class CircleElement extends Element {
    private float radius;
    private float thickness;
    private boolean filled;

    public CircleElement(float radius, float thickness) {
        this.radius = radius;
        this.thickness = thickness;
        this.filled = thickness <= 0;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, Vec3 centerPos, float partialTicks) {
        poseStack.pushPose();
        applyTransformations(poseStack);

        // Use different render types based on whether it's filled or not
        VertexConsumer consumer = buffer.getBuffer(filled ? RenderType.debugQuads() : RenderType.lines());
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal(); // Add normal matrix for proper rendering

        int segments = Math.max(16, (int)(radius * 4));
        float angleStep = 2 * (float)Math.PI / segments;

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = (color.getAlpha() / 255f) * alpha;

        if (filled) {
            // Render filled circle using triangles
            for (int i = 0; i < segments; i++) {
                float angle1 = i * angleStep;
                float angle2 = (i + 1) * angleStep;

                float x1 = (float)Math.cos(angle1) * radius;
                float y1 = (float)Math.sin(angle1) * radius;
                float x2 = (float)Math.cos(angle2) * radius;
                float y2 = (float)Math.sin(angle2) * radius;

                // Triangle from center to edge
                consumer.vertex(matrix, 0, 0, 0).color(r, g, b, a).uv(0.5f, 0.5f).normal(normalMatrix, 0, 0, 1).endVertex();
                consumer.vertex(matrix, x1, y1, 0).color(r, g, b, a).uv(0.5f + x1/radius * 0.5f, 0.5f + y1/radius * 0.5f).normal(normalMatrix, 0, 0, 1).endVertex();
                consumer.vertex(matrix, x2, y2, 0).color(r, g, b, a).uv(0.5f + x2/radius * 0.5f, 0.5f + y2/radius * 0.5f).normal(normalMatrix, 0, 0, 1).endVertex();
                consumer.vertex(matrix, x2, y2, 0).color(r, g, b, a).uv(0.5f + x2/radius * 0.5f, 0.5f + y2/radius * 0.5f).normal(normalMatrix, 0, 0, 1).endVertex();
            }
        } else {
            // Render circle outline using lines
            for (int i = 0; i < segments; i++) {
                float angle1 = i * angleStep;
                float angle2 = (i + 1) * angleStep;

                float x1 = (float)Math.cos(angle1) * radius;
                float y1 = (float)Math.sin(angle1) * radius;
                float x2 = (float)Math.cos(angle2) * radius;
                float y2 = (float)Math.sin(angle2) * radius;

                // Add normal vectors for lines
                consumer.vertex(matrix, x1, y1, 0).color(r, g, b, a).normal(normalMatrix, 0, 0, 1).endVertex();
                consumer.vertex(matrix, x2, y2, 0).color(r, g, b, a).normal(normalMatrix, 0, 0, 1).endVertex();
            }
        }

        renderChildren(poseStack, buffer, centerPos, partialTicks);
        poseStack.popPose();
    }

    @Override
    public ElementType getElementType() {
        return ElementType.CIRCLE;
    }

    public float getRadius() {
        return radius;
    }

    public boolean isFilled() {
        return this.filled;
    }

    public float getThickness() { return thickness; }
    public void setRadius(float radius) { this.radius = radius; }
    public void setThickness(float thickness) {
        this.thickness = thickness;
        this.filled = thickness <= 0;
    }
}