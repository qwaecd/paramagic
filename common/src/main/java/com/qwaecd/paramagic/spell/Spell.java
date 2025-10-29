package com.qwaecd.paramagic.spell;


import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import lombok.Getter;

public class Spell {
    @Getter
    private final String ID;
    private final SpellStateMachine stateMachine;

    public Spell(String ID, SpellConfiguration cfg) {
        this.ID = ID;
        this.stateMachine = new SpellStateMachine(cfg);
    }

    public void tick(float deltaTime) {
        this.stateMachine.update(deltaTime);
    }

    public boolean isCompleted() {
        return this.stateMachine.isCompleted();
    }

    public void interrupt() {
        this.stateMachine.forceInterrupt();
    }

    public void addListener(ISpellPhaseListener listener) {
        this.stateMachine.addListener(listener);
    }

    public static class Builder {
        private SpellConfiguration spellConfiguration;
        private final String id;

        public Builder(String id) {
            this.id = id;
        }

        public Builder addPhase(PhaseConfig cfg) {
            if (this.spellConfiguration == null) {
                this.spellConfiguration = new SpellConfiguration(cfg);
            } else {
                this.spellConfiguration.addPhaseConfig(cfg);
            }
            return this;
        }

        public Spell build() {
            return new Spell(id, this.spellConfiguration);
        }
    }
}
