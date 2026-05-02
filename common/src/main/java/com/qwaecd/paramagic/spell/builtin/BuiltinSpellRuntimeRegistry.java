package com.qwaecd.paramagic.spell.builtin;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.builtin.explostion.ExplosionSpellRuntime;
import com.qwaecd.paramagic.spell.server.SpellRuntime;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@PlatformScope(PlatformScopeType.COMMON)
public final class BuiltinSpellRuntimeRegistry {
    private static BuiltinSpellRuntimeRegistry INSTANCE;

    private final Map<BuiltinSpellId, Supplier<SpellRuntime>> registry = new HashMap<>();

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new BuiltinSpellRuntimeRegistry();
            registerAll();
        }
    }

    private static void registerAll() {
        register(BuiltinSpellIds.EXPLOSION, ExplosionSpellRuntime::new);
    }

    public static synchronized void register(BuiltinSpellId spellId, Supplier<SpellRuntime> runtimeFactory) {
        INSTANCE.registry.put(spellId, runtimeFactory);
    }

    @Nullable
    public static SpellRuntime create(BuiltinSpellId spellId) {
        Supplier<SpellRuntime> factory = INSTANCE.registry.get(spellId);
        if (factory == null) {
            return null;
        }
        return factory.get();
    }

    public static boolean contains(BuiltinSpellId spellId) {
        return INSTANCE.registry.containsKey(spellId);
    }
}
