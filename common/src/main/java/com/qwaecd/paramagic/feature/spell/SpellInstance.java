package com.qwaecd.paramagic.feature.spell;


import com.qwaecd.paramagic.feature.spell.state.SpellStateMachine;

public class SpellInstance {
    private final SpellStateMachine stateMachine;

    public SpellInstance(SpellConfiguration cfg) {
        this.stateMachine = new SpellStateMachine(cfg);
    }
}
