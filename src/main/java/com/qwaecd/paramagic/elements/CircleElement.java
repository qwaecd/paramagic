package com.qwaecd.paramagic.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

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

        VertexConsumer consumer = buffer.getBuffer(RenderType.lines());
        Matrix4f matrix = poseStack.last().pose();

        int segments = Math.max(16, (int)(radius * 4));
        float angleStep = 2 * (float)Math.PI / segments;

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = (color.getAlpha() / 255f) * alpha;

        if (filled) {
            // Render filled circle
            for (int i = 0; i < segments; i++) {
                float angle1 = i * angleStep;
                float angle2 = (i + 1) * angleStep;

                float x1 = (float)Math.cos(angle1) * radius;
                float y1 = (float)Math.sin(angle1) * radius;
                float x2 = (float)Math.cos(angle2) * radius;
                float y2 = (float)Math.sin(angle2) * radius;

                consumer.vertex(matrix, 0, 0, 0).color(r, g, b, a).endVertex();
                consumer.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex();
                consumer.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex();
                consumer.vertex(matrix, x2, y2, 0).color(r, g, b, a).endVertex();
            }
        } else {
            // Render circle outline
            for (int i = 0; i < segments; i++) {
                float angle1 = i * angleStep;
                float angle2 = (i + 1) * angleStep;

                float x1 = (float)Math.cos(angle1) * radius;
                float y1 = (float)Math.sin(angle1) * radius;
                float x2 = (float)Math.cos(angle2) * radius;
                float y2 = (float)Math.sin(angle2) * radius;

                consumer.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex();
                consumer.vertex(matrix, x2, y2, 0).color(r, g, b, a).endVertex();
            }
        }

        renderChildren(poseStack, buffer, centerPos, partialTicks);
        poseStack.popPose();
    }

    public void setRadius(float radius) { this.radius = radius; }
    public void setThickness(float thickness) {
        this.thickness = thickness;
        this.filled = thickness <= 0;
    }
}
