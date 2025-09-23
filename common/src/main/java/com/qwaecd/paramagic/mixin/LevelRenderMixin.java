package com.qwaecd.paramagic.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.core.particle.ParticleManager;
import com.qwaecd.paramagic.core.render.RendererManager;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.context.RenderContextManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRenderMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "renderLevel",
            at = @At("RETURN")
    )
    private void renderLevelMixin(
            PoseStack poseStack,
            float partialTick,
            long finishNanoTime,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightTexture,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        ModRenderSystem rs = ModRenderSystem.getInstance();
        RendererManager rendererManager = rs.getRendererManager();
        ParticleManager particleManager = rs.getParticleManager();
        if (!minecraft.isPaused()) {
            Timer timer = ((MinecraftMixin) minecraft).getTimer();
            // 距离上一帧的时间，单位是游戏刻
            float deltaFrameTime = minecraft.getDeltaFrameTime();
            float secondsPerTick = ((TimerMixin) timer).getMsPerTick() / 1000.0f;
            float deltaTimeInSeconds = deltaFrameTime * secondsPerTick;
            rendererManager.update(deltaTimeInSeconds);
            if (rs.isCanUseComputerShader()) {
                particleManager.update(deltaTimeInSeconds);
            }
        }

        rendererManager.submitAll();
        rs.renderScene(RenderContextManager.getContext());
    }
}
