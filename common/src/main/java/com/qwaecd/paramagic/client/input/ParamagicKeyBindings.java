package com.qwaecd.paramagic.client.input;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.debug.ParamagicDebugState;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.ui.util.UINetwork;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PlatformScope(PlatformScopeType.CLIENT)
public final class ParamagicKeyBindings {
    public static final String CATEGORY = "key.categories.paramagic";

    private static final List<KeyBindingDefinition> DEFINITIONS = new ArrayList<>();
    private static final Set<String> IDS = new HashSet<>();
    private static boolean initialized;

    private ParamagicKeyBindings() {
    }

    public static synchronized List<KeyBindingDefinition> definitions() {
        if (!initialized) {
            registerAll();
            initialized = true;
        }
        return List.copyOf(DEFINITIONS);
    }

    private static void registerAll() {
        if (Paramagic.isDevEnv()) {
            register(new KeyBindingDefinition(
                    "debug.toggle_particle_info",
                    GLFW.GLFW_KEY_P,
                    CATEGORY,
                    () -> ParamagicDebugState.setShowParticleInfo(!ParamagicDebugState.showParticleInfo())
            ));
        }
        register(new KeyBindingDefinition(
                "action.edit_wand",
                GLFW.GLFW_KEY_K,
                CATEGORY,
                UINetwork::requestOpenSpellEditMenu)
        );
    }

    private static void register(KeyBindingDefinition definition) {
        if (!IDS.add(definition.id())) {
            throw new IllegalStateException("Duplicate key binding id: " + definition.id());
        }
        DEFINITIONS.add(definition);
    }
}
