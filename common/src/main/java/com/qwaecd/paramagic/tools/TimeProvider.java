package com.qwaecd.paramagic.tools;

import com.qwaecd.paramagic.mixin.accessor.MinecraftMixin;
import com.qwaecd.paramagic.mixin.accessor.TimerMixin;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.compat.replay.ReplayCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;

import javax.annotation.Nonnull;

@PlatformScope(PlatformScopeType.CLIENT)
public class TimeProvider {
    /**
     * 从 Minecraft 实例中获取每帧的时间增量，单位为秒
     * @param minecraft Minecraft 实例
     * @return 距离上一帧的时间，单位为秒
     */
    public static float getDeltaTime(@Nonnull Minecraft minecraft) {
        Timer timer = ((MinecraftMixin) minecraft).getTimer();
        // 距离上一帧的时间，单位是游戏刻
        float deltaFrameTime = minecraft.getDeltaFrameTime();
        float msPerTick = ((TimerMixin) timer).getMsPerTick();
        float vanillaDeltaTime = 0.0f;
        if (Float.isFinite(deltaFrameTime) && Float.isFinite(msPerTick) && msPerTick > 0.0f) {
            vanillaDeltaTime = deltaFrameTime * msPerTick / 1000.0f;
        }
        return ReplayCompat.getVisualDeltaTime(minecraft, vanillaDeltaTime);
    }
}
