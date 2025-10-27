package com.qwaecd.paramagic.feature.effect.exposion.listener;

import com.qwaecd.paramagic.core.accessor.EntityAccessor;
import com.qwaecd.paramagic.feature.spell.Spell;
import com.qwaecd.paramagic.feature.spell.SpellScheduler;
import com.qwaecd.paramagic.feature.spell.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.feature.spell.state.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;

public class ExplosionBaseListener implements ISpellPhaseListener {
    protected final Spell spell;
    protected final EntityAccessor accessor;


    public ExplosionBaseListener(Spell spell, EntityAccessor accessor) {
        this.spell = spell;
        this.accessor = accessor;
    }
    @Override
    public void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType newPhase) {
    }

    @Override
    public void onTick(float deltaTime) {
    }

    @Override
    public void onEffectTriggered(EffectTriggerPoint triggerPoint) {
    }

    @Override
    public void onSpellInterrupted() {
        SpellScheduler.getINSTANCE().removeSpell(this.spell.getID());
    }

    @Override
    public void onSpellCompleted() {
        SpellScheduler.getINSTANCE().removeSpell(this.spell.getID());
    }
}
