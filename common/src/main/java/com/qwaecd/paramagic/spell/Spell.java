package com.qwaecd.paramagic.spell;


import com.qwaecd.paramagic.data.SpellAssets;
import com.qwaecd.paramagic.spell.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import lombok.Getter;

import javax.annotation.Nonnull;

@SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
public class Spell {
    @Getter
    private final String id;
    private final SpellStateMachine stateMachine;

    @Nonnull
    private final SpellAssets spellAssets;

    public Spell(String id, @Nonnull SpellAssets spellAssets, SpellConfiguration cfg) {
        this.id = id;
        this.spellAssets = spellAssets;
        this.stateMachine = new SpellStateMachine(cfg);
    }

    public void postEvent(MachineEvent event) {
        this.stateMachine.postEvent(event);
    }

    public void tick(float deltaTime) {
        this.stateMachine.update(deltaTime);
    }

    public boolean isCompleted() {
        return this.stateMachine.isCompleted();
    }

    public void interrupt() {
        this.stateMachine.interrupt();
    }

    public void forceInterrupt() {
        this.stateMachine.forceInterrupt();
    }

    public void addListener(ISpellPhaseListener listener) {
        this.stateMachine.addListener(listener);
    }

    @Nonnull
    public SpellAssets getSpellAssets() {
        return this.spellAssets;
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

        public Spell build(SpellAssets spellAssets) {
            return new Spell(id, spellAssets, this.spellConfiguration);
        }
    }
}
