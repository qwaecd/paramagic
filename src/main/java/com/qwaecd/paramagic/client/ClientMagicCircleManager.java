package com.qwaecd.paramagic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.api.client.IClientRenderableMagicCircle;

import java.util.ArrayList;
import java.util.List;
@Deprecated
public class ClientMagicCircleManager {
    private static final List<IClientRenderableMagicCircle> activeCircles = new ArrayList<>();

    public static void addCircle(IClientRenderableMagicCircle circle) {
        activeCircles.add(circle);
    }

    public static void tick() {
        activeCircles.forEach(IClientRenderableMagicCircle::tick);
        activeCircles.removeIf(IClientRenderableMagicCircle::isFinished);
    }

    public static void renderAll(PoseStack poseStack, float partialTick) {
        activeCircles.forEach(circle -> circle.render(poseStack, partialTick));
    }
}
