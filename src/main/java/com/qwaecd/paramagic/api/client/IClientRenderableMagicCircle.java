package com.qwaecd.paramagic.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;

public interface IClientRenderableMagicCircle {
    void startRender(BlockPos center, Object... parameters);
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
