package com.qwaecd.paramagic.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

public class ParticleElement extends Element {
    private ParticleOptions particleType;
    private int count;
    private float speed;
    private int lifetime;
    private boolean loop;
    private float spawnTimer;
    private float spawnInterval;

    public ParticleElement(ParticleOptions particleType, int count, float speed, int lifetime, boolean loop) {
        this.particleType = particleType;
        this.count = count;
        this.speed = speed;
        this.lifetime = lifetime;
        this.loop = loop;
        this.spawnInterval = 1f; // Spawn every second
        this.spawnTimer = 0;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        spawnTimer += deltaTime;
        if (spawnTimer >= spawnInterval) {
            spawnParticles();
            spawnTimer = 0;

            if (!loop) {
                visible = false;
            }
        }
    }

    private void spawnParticles() {
        if (Minecraft.getInstance().level == null) return;

        for (int i = 0; i < count; i++) {
            float angle = (float)(Math.random() * 2 * Math.PI);
            float distance = (float)(Math.random() * 2f);

            double x = Math.cos(angle) * distance + offset.x;
            double y = Math.sin(angle) * distance + offset.y;

            double vx = (Math.random() - 0.5) * speed;
            double vy = (Math.random() - 0.5) * speed;
            double vz = (Math.random() - 0.5) * speed;

            Minecraft.getInstance().level.addParticle(particleType, x, y, 0, vx, vy, vz);
        }
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, Vec3 centerPos, float partialTicks) {
        // Particles are handled by the particle system, no direct rendering needed
        poseStack.pushPose();
        renderChildren(poseStack, buffer, centerPos, partialTicks);
        poseStack.popPose();
    }

    public void setParticleType(ParticleOptions particleType) { this.particleType = particleType; }
    public void setCount(int count) { this.count = count; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void setLoop(boolean loop) { this.loop = loop; }
}
