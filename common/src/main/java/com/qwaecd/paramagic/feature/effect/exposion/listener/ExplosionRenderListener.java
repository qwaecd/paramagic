package com.qwaecd.paramagic.feature.effect.exposion.listener;

import com.qwaecd.paramagic.core.accessor.EntityAccessor;
import com.qwaecd.paramagic.feature.effect.ClientEffectManager;
import com.qwaecd.paramagic.feature.effect.exposion.EXPLOSION;
import com.qwaecd.paramagic.feature.spell.Spell;
import com.qwaecd.paramagic.feature.spell.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.feature.spell.state.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;
import org.joml.Vector3f;

public class ExplosionRenderListener extends ExplosionBaseListener implements ISpellPhaseListener {
    private final EXPLOSION explosion;
    public ExplosionRenderListener(Spell spell, EXPLOSION explosion, EntityAccessor accessor) {
        super(spell, accessor);
        this.explosion = explosion;
    }

    @Override
    public void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType newPhase) {
        if (newPhase == SpellPhaseType.CASTING && oldPhase == SpellPhaseType.IDLE) {
            ClientEffectManager.getInstance().addExplosion(this.spell.getID(), this.explosion);
        }
    }

    @Override
    public void onTick(float deltaTime) {
        Vector3f lookAngle = this.accessor.getLookAngle();
        Vector3f eyePosition = this.accessor.getEyePositon();
        Vector3f newEmitterCenter = new Vector3f(
                eyePosition.x + lookAngle.x * 2.2f,
                eyePosition.y + lookAngle.y * 2.2f,
                eyePosition.z + lookAngle.z * 2.2f
        );
        explosion.modifyProps(
                newEmitterCenter,
                eyePosition
        );
    }

    @Override
    public void onEffectTriggered(EffectTriggerPoint triggerPoint) {
    }

    @Override
    public void onSpellInterrupted() {
        ClientEffectManager.getInstance().removeExplosion(this.spell.getID());
    }

    @Override
    public void onSpellCompleted() {
        ClientEffectManager.getInstance().removeExplosion(this.spell.getID());
    }
}
