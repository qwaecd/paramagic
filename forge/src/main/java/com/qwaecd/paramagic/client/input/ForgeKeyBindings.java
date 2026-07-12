package com.qwaecd.paramagic.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.qwaecd.paramagic.Paramagic;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Map;

/** Forge registration and dispatch adapter for {@link ParamagicKeyBindings}. */
@Mod.EventBusSubscriber(modid = Paramagic.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ForgeKeyBindings {
    private static final Map<KeyBindingDefinition, KeyMapping> BINDINGS = new LinkedHashMap<>();
    private static boolean registered;

    private ForgeKeyBindings() {
    }

    public static void registerAll(RegisterKeyMappingsEvent event) {
        if (registered) {
            return;
        }
        registered = true;

        for (KeyBindingDefinition definition : ParamagicKeyBindings.definitions()) {
            KeyMapping keyMapping = new KeyMapping(
                    definition.translationKey(),
                    InputConstants.Type.KEYSYM,
                    definition.defaultKeyCode(),
                    definition.categoryKey()
            );
            BINDINGS.put(definition, keyMapping);
            event.register(keyMapping);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        for (Map.Entry<KeyBindingDefinition, KeyMapping> entry : BINDINGS.entrySet()) {
            while (entry.getValue().consumeClick()) {
                entry.getKey().press();
            }
        }
    }
}
