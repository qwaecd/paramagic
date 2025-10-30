package com.qwaecd.paramagic.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SpellScheduler {
    private static SpellScheduler C_INSTANCE;
    private static SpellScheduler S_INSTANCE;
    private final List<Spell> activeSpells = new ArrayList<>();
    private final Map<String, Spell> spellMap = new HashMap<>();

    private final ConcurrentLinkedQueue<Spell> pendingAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Spell> pendingRemove = new ConcurrentLinkedQueue<>();

    public static void init() {
        if (S_INSTANCE == null) {
            S_INSTANCE = new SpellScheduler();
        }
        if (C_INSTANCE == null) {
            C_INSTANCE = new SpellScheduler();
        }
    }

    public static SpellScheduler getINSTANCE(boolean isClientSide) {
        SpellScheduler instance = isClientSide ? C_INSTANCE : S_INSTANCE;
        if (instance == null) {
            throw new IllegalStateException("SpellScheduler not initialized. Call init() first.");
        }
        return instance;
    }

    public void tick(float deltaTime) {
        Spell s;
        while ((s = pendingAdd.poll()) != null) {
            this.activeSpells.add(s);
            this.registerSpell(s);
        }
        while ((s = pendingRemove.poll()) != null) {
            s.forceInterrupt();
            this.activeSpells.remove(s);
            this.unregisterSpell(s);
        }

        for (Spell spell : this.activeSpells) {
            spell.tick(deltaTime);
            if (spell.isCompleted()){
                this.pendingRemove.add(spell);
            }
        }
    }

    public void addSpell(Spell spell) {
        this.pendingAdd.add(spell);
    }

    public void removeSpell(Spell spell) {
        this.pendingRemove.add(spell);
    }

    public void removeSpell(String ID) {
        Spell spell = this.spellMap.get(ID);
         if (spell == null) {
            return;
        }
        this.pendingRemove.add(spell);
    }

    private void registerSpell(Spell spell) {
        this.spellMap.put(spell.getId(), spell);
    }

    private void unregisterSpell(String ID) {
        this.spellMap.remove(ID);
    }

    private void unregisterSpell(Spell spell) {
        this.spellMap.remove(spell.getId());
    }
}
