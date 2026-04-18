package com.qwaecd.paramagic.world.sound;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SoundHelper {

    @PlatformScope(PlatformScopeType.CLIENT)
    public static void playLocalSound(
            @Nonnull Level level,
            double x, double y, double z,
            @Nullable SoundEvent sound,
            SoundSource category,
            float volume,
            float pitch
    ) {
        if (sound == null || category == null) {
            return;
        }
        level.playSound(
                Minecraft.getInstance().player,
                x, y, z,
                sound,
                category,
                volume,
                pitch
        );
    }
}
