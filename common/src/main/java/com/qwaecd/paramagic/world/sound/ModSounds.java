package com.qwaecd.paramagic.world.sound;

import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
    private ModSounds() {}
    public static SoundEvent LASER;
    public static void init(SoundProvider provider) {
        LASER = create(provider, "laser");
    }

    public interface SoundProvider {
        SoundEvent register(String key);
    }

    public static SoundEvent create(SoundProvider provider, String key) {
        return provider.register(key);
    }
}
