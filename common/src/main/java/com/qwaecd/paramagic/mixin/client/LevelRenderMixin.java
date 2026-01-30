package com.qwaecd.paramagic.mixin.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.RendererManager;
import com.qwaecd.paramagic.core.render.context.RenderContextManager;
import com.qwaecd.paramagic.tools.TimeProvider;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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
        ParticleSystem particleSystem = rs.getParticleSystem();
        if (!minecraft.isPaused()) {
            float deltaTimeInSeconds = TimeProvider.getDeltaTime(minecraft);
            rendererManager.update(deltaTimeInSeconds);
            if (rs.canUseComputerShader()) {
                particleSystem.update(deltaTimeInSeconds);
            }
        }

        rendererManager.submitAll();
        rs.renderScene(RenderContextManager.getContext());
    }
    // 这个函数会在区块数据更新时被调用，可以在这里上传区块数据

    /*@Inject(
            method = "setSectionDirty(IIIZ)V",
            at = @At("HEAD")
    )
    private void setSectionDirtyMixin(int sectionX, int sectionY, int sectionZ, boolean reRenderOnMainThread, CallbackInfo ci) {
        if (this.level == null) {
            return;
        }
        LevelChunk chunk = this.level.getChunk(sectionX, sectionZ);
        LevelChunkSection section = chunk.getSection(sectionY);

        if (section.hasOnlyAir()) {
            return;
        }

        for (int y = 0; y < 16; ++y) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    BlockState blockState = section.getBlockState(x, y, z);
                    if (!blockState.isAir()) {
                        // 上传区块数据到渲染系统
                    }
                }
            }
        }
        MixinCallback.call(sectionX, sectionY, sectionZ, reRenderOnMainThread);
    }*/
}
