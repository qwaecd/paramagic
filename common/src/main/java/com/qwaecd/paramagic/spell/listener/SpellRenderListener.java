package com.qwaecd.paramagic.spell.listener;

import com.qwaecd.paramagic.assembler.AssemblyException;
import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.core.render.TransformSample;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.session.client.ClientSessionListener;
import com.qwaecd.paramagic.spell.session.client.ClientSessionView;
import com.qwaecd.paramagic.spell.state.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Objects;

public class SpellRenderListener implements ClientSessionListener {
    private static final Logger logger = LoggerFactory.getLogger(SpellRenderListener.class);
    private final Spell spell;
    private final RenderCondition renderCondition;

    @Nullable
    private ClientSessionView view;
    private final TransformSample tmpSample = new TransformSample();

    @Nullable
    private MagicCircle magicCircle;

    public SpellRenderListener(RenderCondition renderCondition, Spell spell) {
        this.renderCondition = renderCondition;
        this.spell = spell;
    }

    @Override
    public void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType currentPhase) {
        if (this.renderCondition.shouldRender(oldPhase, currentPhase)) {
            try {
                this.magicCircle = ParaComposer.assemble(spell.getSpellAssets());
            } catch (AssemblyException e) {
                logger.warn("Failed to assemble spell assets for rendering", e);
                return;
            }
            if (magicCircle != null) {
                flushTFSource();
                magicCircle.transform.setPosition(tmpSample.position.x, tmpSample.position.y + 0.01f, tmpSample.position.z);
                MagicCircleManager.getInstance().addCircle(this.magicCircle);
            }
        }
    }

    @Override
    public void onTick(SpellPhaseType currentPhase, float deltaTime) {
    }

    @Override
    public void onEffectTriggered(EffectTriggerPoint triggerPoint) {

    }

    @Override
    public void onSpellInterrupted() {
        MagicCircleManager.getInstance().removeCircle(this.magicCircle);
    }

    @Override
    public void onSpellCompleted() {
        MagicCircleManager.getInstance().removeCircle(this.magicCircle);
    }

    @Override
    public void bind(ClientSessionView view) {
        if (this.view != null)
            throw new IllegalStateException("already bound");
        this.view = Objects.requireNonNull(view);
    }

    @Override
    public void onSessionClose() {
        MagicCircleManager.getInstance().removeCircle(this.magicCircle);
    }

    private ClientSessionView v() {
        if (view == null)
            throw new NullPointerException("not bound yet");
        return view;
    }

    private void flushTFSource() {
        this.v().casterSource().applyTo(this.tmpSample);
    }
}
