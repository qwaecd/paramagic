package com.qwaecd.paramagic.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

import java.util.LinkedHashMap;
import java.util.Map;

/** Fabric registration and dispatch adapter for {@link ParamagicKeyBindings}. */
public final class FabricKeyBindings {
    private static final Map<KeyBindingDefinition, KeyMapping> BINDINGS = new LinkedHashMap<>();
    private static boolean initialized;

    private FabricKeyBindings() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;

        for (KeyBindingDefinition definition : ParamagicKeyBindings.definitions()) {
            KeyMapping keyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                    definition.translationKey(),
                    InputConstants.Type.KEYSYM,
                    definition.defaultKeyCode(),
                    definition.categoryKey()
            ));
            BINDINGS.put(definition, keyMapping);
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> dispatchPressedKeys());
    }

    private static void dispatchPressedKeys() {
        for (Map.Entry<KeyBindingDefinition, KeyMapping> entry : BINDINGS.entrySet()) {
            while (entry.getValue().consumeClick()) {
                entry.getKey().press();
            }
        }
    }
}
