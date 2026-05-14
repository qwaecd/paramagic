package com.qwaecd.paramagic.compat.replay;

import net.minecraft.client.Minecraft;

public final class ReplayCompat {
    private ReplayCompat() {
    }

    public static boolean shouldPauseVisuals(Minecraft minecraft) {
        return minecraft.isPaused();
    }

    public static float getVisualDeltaTime(Minecraft minecraft, float vanillaDeltaTime) {
        return vanillaDeltaTime;
    }
}
