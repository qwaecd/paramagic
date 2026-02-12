package com.qwaecd.paramagic.spell.builtin;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.builtin.explostion.ExplosionExecutor;
import com.qwaecd.paramagic.spell.builtin.explostion.ExplosionSpell;
import com.qwaecd.paramagic.spell.session.server.SpellExecutor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@PlatformScope(PlatformScopeType.COMMON)
public class BuiltinSpellRegistry {
    private static BuiltinSpellRegistry INSTANCE;

    private final Map<BuiltinSpellId, BuiltinSpellEntry> registry = new HashMap<>();

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new BuiltinSpellRegistry();
            registerAll();
        }
    }

    private static void registerAll() {
        register(ExplosionSpell.SPELL_ID, new ExplosionSpell(), ExplosionExecutor::new);
    }

    public static synchronized void register(BuiltinSpellId spellId, BuiltinSpell spell, Supplier<SpellExecutor> executorFactory) {
        Supplier<SpellExecutor> factory = Objects.requireNonNullElseGet(executorFactory, () -> SpellExecutor::new);
        INSTANCE.registry.put(spellId, new BuiltinSpellEntry(spell, factory));
    }

    @Nullable
    public static BuiltinSpellEntry getSpell(BuiltinSpellId spellId) {
        return INSTANCE.registry.get(spellId);
    }

    public static boolean containsSpell(BuiltinSpellId spellId) {
        return INSTANCE.registry.containsKey(spellId);
    }
}
