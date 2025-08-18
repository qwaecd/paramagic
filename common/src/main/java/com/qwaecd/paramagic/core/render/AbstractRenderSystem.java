package com.qwaecd.paramagic.core.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;

public abstract class AbstractRenderSystem extends RenderSystem {
    public final float getMaxDistance() {
        return Minecraft.getInstance().options.getEffectiveRenderDistance();
    }
    public final RenderTarget mainTarget() {
        return Minecraft.getInstance().getMainRenderTarget();
    }
}
