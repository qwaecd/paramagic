package com.qwaecd.paramagic.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.qwaecd.paramagic.api.animation.AnimationKeyframe;
import com.qwaecd.paramagic.api.animation.AnimationTimeline;
import com.qwaecd.paramagic.resource.ModResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Element {
    protected Vector2f offset = new Vector2f(0, 0); //偏移
    protected Vector2f scale = new Vector2f(1, 1);  //缩放
    protected float rotation = 0f;
    protected float rotationSpeed = 0f;
    protected Color color = new Color(0, 255, 233, 179);
    protected float alpha = 1f;
    protected int zOrder = 0;
    protected List<Element> children = new ArrayList<>();
    protected AnimationTimeline animation;
    protected boolean visible = true;   //是否可见

    public void update(float deltaTime) {
        // Update rotation
        if (rotationSpeed != 0) {
            rotation += rotationSpeed * deltaTime;
            rotation = rotation % 360f;
        }

        // Update animation
        if (animation != null) {
            animation.update(deltaTime);
            updateFromAnimation();
        }

        // Update children
        for (Element child : children) {
            child.update(deltaTime);
        }
    }

    protected void updateFromAnimation() {
        if (animation == null || !animation.isActive()) return;

        AnimationKeyframe offsetKf = animation.getKeyframe("offset");
        if (offsetKf != null) {
            offset = (Vector2f)offsetKf.interpolate(animation.getProgress());
        }

        AnimationKeyframe rotationKf = animation.getKeyframe("rotation");
        if (rotationKf != null) {
            rotation = (Float)rotationKf.interpolate(animation.getProgress());
        }

        AnimationKeyframe scaleKf = animation.getKeyframe("scale");
        if (scaleKf != null) {
            scale = (Vector2f)scaleKf.interpolate(animation.getProgress());
        }

        AnimationKeyframe colorKf = animation.getKeyframe("color");
        if (colorKf != null) {
            color = (Color)colorKf.interpolate(animation.getProgress());
        }

        AnimationKeyframe alphaKf = animation.getKeyframe("alpha");
        if (alphaKf != null) {
            alpha = (Float)alphaKf.interpolate(animation.getProgress());
        }
    }

    public abstract void render(PoseStack poseStack, MultiBufferSource buffer, Vec3 centerPos, float partialTicks);

    protected void applyTransformations(PoseStack poseStack) {
        Vec3 position = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(-offset.x-position.x(), -position.y(), -offset.y-position.z());
        poseStack.mulPose(new Quaternionf(1, 0, 0, 1));
        if (rotation != 0) {
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(rotation));
        }
        if (scale.x != 1f || scale.y != 1f) {
            poseStack.scale(scale.x, scale.y, 1f);
        }
    }

    protected void renderChildren(PoseStack poseStack, MultiBufferSource buffer, Vec3 centerPos, float partialTicks) {
        for (Element child : children) {
            if (child.visible) {
                poseStack.pushPose();
                child.render(poseStack, buffer, centerPos, partialTicks);
                poseStack.popPose();
            }
        }
    }

    // Getters and setters
    public abstract ElementType getElementType();
    public void addChild(Element child) { children.add(child); }
    public void setAnimation(AnimationTimeline animation) { this.animation = animation; }
    public void setOffset(float x, float y) { offset.set(x, y); }
    public void setScale(float x, float y) { scale.set(x, y); }
    public void setRotation(float rotation) { this.rotation = rotation; }
    public void setRotationSpeed(float speed) { this.rotationSpeed = speed; }
    public void setColor(Color color) { this.color = color; }
    public void setAlpha(float alpha) { this.alpha = alpha; }
    public void setZOrder(int zOrder) { this.zOrder = zOrder; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public enum ElementType{
        CIRCLE,
        GROUP,
        LINE,
        PARTICLE,
        RUNE,
        TEXT;
    }
}
