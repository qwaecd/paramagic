package com.qwaecd.paramagic.spell.builtin;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.SpellIdentifier;
import com.qwaecd.paramagic.spell.builtin.impl.ExplosionSpell;
import com.qwaecd.paramagic.spell.builtin.impl.ExplosionSpellVisual;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PlatformScope(PlatformScopeType.CLIENT)
public class BuiltinSpellVisualRegistry {
    private static BuiltinSpellVisualRegistry INSTANCE;

    private final Map<SpellIdentifier, BuiltinSpellVisual> registry = new ConcurrentHashMap<>();

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new BuiltinSpellVisualRegistry();
            registerAll();
        }
    }

    private static void registerAll() {
        register(ExplosionSpell.SPELL_ID, new ExplosionSpellVisual());
    }

    public static void register(SpellIdentifier spellId, BuiltinSpellVisual spell) {
        INSTANCE.registry.put(spellId, spell);
    }

    public static void unregister(SpellIdentifier spellId) {
        INSTANCE.registry.remove(spellId);
    }

    @Nullable
    public static BuiltinSpellVisual getSpell(SpellIdentifier spellId) {
        return INSTANCE.registry.get(spellId);
    }

    public static boolean containsSpell(SpellIdentifier spellId) {
        return INSTANCE.registry.containsKey(spellId);
    }
}
