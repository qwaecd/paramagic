package com.qwaecd.paramagic.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.client.renderer.MagicCircleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.qwaecd.paramagic.Paramagic.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientRenderEvents {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            renderMagicCircles(event);
        }
    }

    private static void renderMagicCircles(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        // Get camera position for coordinate transformation
        Vec3 cameraPos = event.getCamera().getPosition();

        poseStack.pushPose();

        // Transform to world-relative coordinates
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        // Render all active magic circles
        MagicCircleManager.renderAll(poseStack, bufferSource);

        // End batch to submit all rendering
        bufferSource.endBatch();

        poseStack.popPose();
    }
}