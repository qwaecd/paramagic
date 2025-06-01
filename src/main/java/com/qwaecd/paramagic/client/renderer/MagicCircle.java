package com.qwaecd.paramagic.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.UUID;

public class MagicCircle {
    public enum State {
        BUILDING,
        MAINTAINING,
        DISSIPATING
    }

    private final UUID id;
    private final Vec3 position;
    private final float yaw;
    private final float pitch;
    private final ResourceLocation texture;
    private float radius;
    private State state;
    private int age;
    private boolean finished;

    // Animation durations (in ticks)
    private final int buildDuration;
    private final int maintainDuration;
    private final int dissipateDuration;

    // Animation parameters
    private float currentRadius;
    private float alpha;
    private float rotationSpeed;
    private float currentRotation;

    public MagicCircle(UUID id, BlockPos position, float yaw, float pitch,
                       ResourceLocation texture, float radius) {
        this.id = id;
        this.position = position.getCenter();
        this.yaw = yaw;
        this.pitch = pitch;
        this.texture = texture;
        this.radius = radius;
        this.state = State.BUILDING;
        this.age = 0;
        this.finished = false;

        // Default durations
        this.buildDuration = 20;  // 1 second
        this.maintainDuration = 60; // 3 seconds
        this.dissipateDuration = 20; // 1 second

        // Initialize animation parameters
        this.currentRadius = 0.0f;
        this.alpha = 0.0f;
        this.rotationSpeed = 2.0f;
        this.currentRotation = 0.0f;
    }

    public void tick() {
        age++;
        currentRotation += rotationSpeed;
        if (currentRotation >= 360.0f) {
            currentRotation -= 360.0f;
        }

        switch(state) {
            case BUILDING:
                float buildProgress = Math.min(1.0f, (float) age / buildDuration);
                currentRadius = radius * buildProgress;
                alpha = buildProgress * 0.8f;

                if (age > buildDuration) {
                    state = State.MAINTAINING;
                    age = 0;
                }
                break;

            case MAINTAINING:
                currentRadius = radius;
                alpha = 0.8f + 0.2f * (float) Math.sin(age * 0.1f); // Gentle pulsing

                if (age > maintainDuration) {
                    state = State.DISSIPATING;
                    age = 0;
                }
                break;

            case DISSIPATING:
                float dissipateProgress = Math.min(1.0f, (float) age / dissipateDuration);
                currentRadius = radius * (1.0f - dissipateProgress * 0.3f);
                alpha = 0.8f * (1.0f - dissipateProgress);

                if (age > dissipateDuration) {
                    finished = true;
                }
                break;
        }
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        if (finished || alpha <= 0.0f) {
            return;
        }

        poseStack.pushPose();

        // Transform to magic circle coordinate system
        poseStack.translate(position.x, position.y, position.z);
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(yaw)));
        poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-pitch)));
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(currentRotation)));

        // Get the vertex consumer for our custom render type
        VertexConsumer vertexConsumer = bufferSource.getBuffer(MagicCircleRenderer.MAGIC_CIRCLE_TYPE);

        // Render the magic circle geometry
        MagicCircleRenderer.renderCircle(this, poseStack, vertexConsumer);

        poseStack.popPose();
    }

    // Getters
    public UUID getId() { return id; }
    public Vec3 getPos() { return position; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public ResourceLocation getTexture() { return texture; }
    public float getRadius() { return radius; }
    public float getCurrentRadius() { return currentRadius; }
    public State getState() { return state; }
    public int getAge() { return age; }
    public boolean isFinished() { return finished; }
    public float getAlpha() { return alpha; }
    public float getCurrentRotation() { return currentRotation; }

    // Setters for dynamic modification
    public void setRadius(float radius) { this.radius = radius; }
    public void setState(State state) { this.state = state; this.age = 0; }
}