package com.qwaecd.paramagic.feature.spell;


import com.qwaecd.paramagic.feature.spell.state.SpellStateMachine;

public class Spell {
    private final SpellStateMachine stateMachine;

    public Spell(SpellConfiguration cfg) {
        this.stateMachine = new SpellStateMachine(cfg);
    }

    public void tick(float deltaTime) {
        this.stateMachine.update(deltaTime);
    }
}
