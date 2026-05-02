package com.qwaecd.paramagic.spell.api;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.builtin.AllBuiltinSpellIds;
import com.qwaecd.paramagic.spell.builtin.explostion.ExplosionSpellPresentation;
import com.qwaecd.paramagic.spell.client.SpellPresentation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@PlatformScope(PlatformScopeType.CLIENT)
public final class AllPresentations {
    private static AllPresentations INSTANCE;

    private final Map<BuiltinSpellId, Supplier<SpellPresentation>> registry = new HashMap<>();

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new AllPresentations();
            registerAll();
        }
    }

    private static void registerAll() {
        register(AllBuiltinSpellIds.EXPLOSION, ExplosionSpellPresentation::new);
    }

    public static synchronized void register(BuiltinSpellId spellId, Supplier<SpellPresentation> presentationFactory) {
        INSTANCE.registry.put(spellId, presentationFactory);
    }

    @Nullable
    public static SpellPresentation create(BuiltinSpellId spellId) {
        Supplier<SpellPresentation> factory = INSTANCE.registry.get(spellId);
        if (factory == null) {
            return null;
        }
        return factory.get();
    }
}
