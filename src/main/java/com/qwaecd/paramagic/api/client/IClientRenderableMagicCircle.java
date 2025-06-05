package com.qwaecd.paramagic.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;

public interface IClientRenderableMagicCircle {
    void startRender(Vec3 center, Object... parameters);

    void tick();

    void render(PoseStack poseStack, float partialTick);

    boolean isFinished();

    Phase getCurrentPhase();

    enum Phase {
        BUILD,      // 构建
        SUSTAIN,    // 维持
        DISSIPATE   // 消散
    }
}
