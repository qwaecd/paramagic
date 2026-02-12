package com.qwaecd.paramagic.spell.builtin.client;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.builtin.explostion.ExplosionSpell;
import com.qwaecd.paramagic.spell.builtin.explostion.ExplosionSpellRenderer;
import com.qwaecd.paramagic.spell.builtin.explostion.ExplosionSpellVisual;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@PlatformScope(PlatformScopeType.CLIENT)
public class BuiltinSpellVisualRegistry {
    private static BuiltinSpellVisualRegistry INSTANCE;

    private final Map<BuiltinSpellId, VisualEntry> registry = new HashMap<>();

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new BuiltinSpellVisualRegistry();
            registerAll();
        }
    }

    private static void registerAll() {
        register(ExplosionSpell.SPELL_ID, new ExplosionSpellVisual(), ExplosionSpellRenderer::new);
    }

    public static synchronized void register(BuiltinSpellId spellId, BuiltinSpellVisual visual, Supplier<SpellRenderer> factory) {
        Supplier<SpellRenderer> rendererFactory = Objects.requireNonNullElseGet(factory, () -> SpellRenderer::new);
        INSTANCE.registry.put(spellId, new VisualEntry(visual, rendererFactory));
    }

    @Nullable
    public static VisualEntry getSpell(BuiltinSpellId spellId) {
        return INSTANCE.registry.get(spellId);
    }

    public static boolean containsSpell(BuiltinSpellId spellId) {
        return INSTANCE.registry.containsKey(spellId);
    }
}
