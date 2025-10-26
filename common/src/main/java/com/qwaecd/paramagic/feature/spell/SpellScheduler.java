package com.qwaecd.paramagic.feature.spell;

import java.util.ArrayList;
import java.util.List;

public class SpellScheduler {
    private static SpellScheduler INSTANCE;
    private final List<Spell> activeSpells = new ArrayList<>();

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new SpellScheduler();
        }
    }

    public static SpellScheduler getINSTANCE() {
        if (INSTANCE == null) {
            throw new IllegalStateException("SpellScheduler not initialized. Call init() first.");
        }
        return INSTANCE;
    }

    public void tick(float deltaTime) {
        for (Spell activeSpell : this.activeSpells) {
            activeSpell.tick(deltaTime);
        }
    }
}
