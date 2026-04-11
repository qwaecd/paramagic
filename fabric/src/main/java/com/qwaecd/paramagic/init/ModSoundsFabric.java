package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.sound.ModSounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSoundsFabric {

    public static void registerAll() {
        ModSounds.SoundProvider provider = new ModSounds.SoundProvider() {
            @Override
            public SoundEvent register(String key) {
                ResourceLocation location = ModRL.inModSpace(key);
                return Registry.register(BuiltInRegistries.SOUND_EVENT, location, SoundEvent.createVariableRangeEvent(location));
            }
        };
        ModSounds.init(provider);
    }
}
