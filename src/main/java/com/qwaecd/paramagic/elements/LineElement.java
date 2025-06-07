package com.qwaecd.paramagic.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class LineElement extends Element {
    private Vector2f start;
    private Vector2f end;
    private float thickness;

    public LineElement(Vector2f start, Vector2f end, float thickness) {
        this.start = new Vector2f(start);
        this.end = new Vector2f(end);
        this.thickness = thickness;
    }

    public LineElement(float length, float angle, float thickness) {
        this.start = new Vector2f(0, 0);
        this.end = new Vector2f(
                (float)Math.cos(Math.toRadians(angle)) * length,
                (float)Math.sin(Math.toRadians(angle)) * length
        );
        this.thickness = thickness;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, Vec3 centerPos, float partialTicks) {
        poseStack.pushPose();
        applyTransformations(poseStack);

        VertexConsumer consumer = buffer.getBuffer(RenderType.lines());
        Matrix4f matrix = poseStack.last().pose();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = (color.getAlpha() / 255f) * alpha;

        consumer.vertex(matrix, start.x, start.y, 0).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, end.x, end.y, 0).color(r, g, b, a).endVertex();

        renderChildren(poseStack, buffer, centerPos, partialTicks);
        poseStack.popPose();
    }

    @Override
    public ElementType getElementType() {
        return ElementType.LINE;
    }

    public Vector2f getStart() { return new Vector2f(start); }
    public Vector2f getEnd() { return new Vector2f(end); }
    public float getThickness() { return thickness; }

    public void setStart(float x, float y) { start.set(x, y); }
    public void setEnd(float x, float y) { end.set(x, y); }
    public void setThickness(float thickness) { this.thickness = thickness; }
}
