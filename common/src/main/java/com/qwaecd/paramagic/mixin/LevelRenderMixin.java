package com.qwaecd.paramagic.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.context.impl.BasedContext;
import com.qwaecd.paramagic.core.render.context.RenderContextManager;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRenderMixin {
    @Inject(
            method = "renderLevel",
            at = @At(value = "RETURN")
    )
    private void renderLevelMixin(
            DeltaTracker deltaTracker,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightTexture,
            Matrix4f frustumMatrix,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        matrix4fstack.pushMatrix();
        matrix4fstack.mul(frustumMatrix);
        RenderSystem.applyModelViewMatrix();

        BasedContext basedContext = new BasedContext(camera, RenderSystem.getModelViewStack(), projectionMatrix);
        RenderContextManager.setContext(basedContext);
        try {
            ModRenderSystem rs = ModRenderSystem.getInstance();
            rs.getRendererManager().submitAll();
            rs.renderScene(RenderContextManager.getContext());
        } finally {
            matrix4fstack.popMatrix();
            RenderSystem.applyModelViewMatrix();
        }
    }
}
