package com.qwaecd.paramagic.tools;

import com.qwaecd.paramagic.mixin.accessor.MinecraftMixin;
import com.qwaecd.paramagic.mixin.accessor.TimerMixin;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
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
        float secondsPerTick = ((TimerMixin) timer).getMsPerTick() / 1000.0f;
        return deltaFrameTime * secondsPerTick;
    }
}
