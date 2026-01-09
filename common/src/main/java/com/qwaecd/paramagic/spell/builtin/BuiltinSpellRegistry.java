package com.qwaecd.paramagic.spell.builtin;

import com.qwaecd.paramagic.spell.SpellIdentifier;
import com.qwaecd.paramagic.spell.builtin.impl.ExplosionSpell;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BuiltinSpellRegistry {
    private static BuiltinSpellRegistry INSTANCE;

    private final Map<SpellIdentifier, BuiltinSpell> registry = new ConcurrentHashMap<>();

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new BuiltinSpellRegistry();
            registerAll();
        }
    }

    private static void registerAll() {
        register(ExplosionSpell.SPELL_ID, new ExplosionSpell());
    }

    public static void register(SpellIdentifier spellId, BuiltinSpell spell) {
        INSTANCE.registry.put(spellId, spell);
    }

    public static void unregister(SpellIdentifier spellId) {
        INSTANCE.registry.remove(spellId);
    }

    @Nullable
    public static BuiltinSpell getSpell(SpellIdentifier spellId) {
        return INSTANCE.registry.get(spellId);
    }

    public static boolean containsSpell(SpellIdentifier spellId) {
        return INSTANCE.registry.containsKey(spellId);
    }
}
