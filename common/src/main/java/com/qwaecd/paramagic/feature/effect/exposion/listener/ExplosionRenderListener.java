package com.qwaecd.paramagic.feature.effect.exposion.listener;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.assembler.AssemblyException;
import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.core.accessor.EntityAccessor;
import com.qwaecd.paramagic.data.animation.struct.AnimationBindingConfig;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.feature.circle.CircleMap;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.feature.effect.ClientEffectManager;
import com.qwaecd.paramagic.feature.effect.exposion.EXPLOSION;
import com.qwaecd.paramagic.feature.effect.exposion.ExplosionParaNode;
import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.spell.state.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;
import org.joml.Vector3f;

public class ExplosionRenderListener extends ExplosionBaseListener implements ISpellPhaseListener {
    private final EXPLOSION explosion;
    public ExplosionRenderListener(Spell spell, EXPLOSION explosion, EntityAccessor accessor) {
        super(spell, accessor);
        this.explosion = explosion;
    }

    @Override
    public void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType currentPhase) {
        if (currentPhase == SpellPhaseType.CHANNELING && oldPhase == SpellPhaseType.CASTING) {
            ClientEffectManager.getInstance().addExplosion(this.spell.getId(), this.explosion);
        }
        ParaData paraData = this.spell.getSpellAssets().getParaData();
        if (currentPhase == SpellPhaseType.CASTING && oldPhase == SpellPhaseType.IDLE) {
            try {
                AnimationBindingConfig anim = this.spell.getSpellAssets().getAnimBindingConfig();

                ParaComposer composer = ParaComposer.getINSTANCE();
                MagicCircle circle = composer.assemble(paraData, anim, null);
                CircleMap.register(spell.getId(), circle);
                MagicCircleManager.getInstance().addCircle(circle);

                final float scale = 1.0f;
                Vector3f eyePosition = this.accessor.getEyePosition();
                circle.getTransform()
                        .setPosition(eyePosition.x, eyePosition.y - 1.6f, eyePosition.z)
                        .setRotationDegrees(0.0f, 0.0f, 0.0f)
                        .setScale(scale);
            } catch (AssemblyException e) {
                Paramagic.LOG.error("Failed to assemble explosion para data.", e);
            }
        }
    }

    @Override
    public void onTick(SpellPhaseType currentPhase, float deltaTime) {
/*        Vector3f lookAngle = this.accessor.getLookAngle();
        Vector3f eyePosition = this.accessor.getEyePosition();
        Vector3f newEmitterCenter = new Vector3f(
                eyePosition.x + lookAngle.x * 2.2f,
                eyePosition.y + lookAngle.y * 2.2f,
                eyePosition.z + lookAngle.z * 2.2f
        );
        explosion.modifyProps(
                newEmitterCenter,
                eyePosition
        );*/
    }

    private void cleanup() {
        MagicCircle circle = CircleMap.unregister(this.spell.getId());
        if (circle != null) {
            MagicCircleManager.getInstance().removeCircle(circle);
        }
//        ClientEffectManager.getInstance().removeExplosion(this.spell.getId());
    }

    @Override
    public void onEffectTriggered(EffectTriggerPoint triggerPoint) {
    }

    @Override
    public void onSpellInterrupted() {
        cleanup();
    }

    @Override
    public void onSpellCompleted() {
        cleanup();
    }
}
