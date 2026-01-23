package com.qwaecd.paramagic.spell.builtin.impl;

import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.feature.effect.exposion.ExplosionAssets;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.SpellIdentifier;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpellVisual;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.session.SpellSessionRef;
import com.qwaecd.paramagic.spell.session.client.ClientSession;
import com.qwaecd.paramagic.spell.session.client.ClientSessionListener;
import com.qwaecd.paramagic.spell.session.client.ClientSessionView;
import com.qwaecd.paramagic.spell.session.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.session.store.SessionDataStore;
import com.qwaecd.paramagic.spell.session.store.SessionDataValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;


@PlatformScope(PlatformScopeType.CLIENT)
public class ExplosionSpellVisual implements BuiltinSpellVisual {
    @Override
    public SpellIdentifier getSpellId() {
        return ExplosionSpell.SPELL_ID;
    }

    @Override
    public void applyVisual(ClientSession session, SpellSessionRef ref) {
        session.registerListener(new VisualListener());
    }

    public static class VisualListener implements ClientSessionListener {
        private static final Logger LOGGER = LoggerFactory.getLogger(VisualListener.class);
        private MagicCircle circle;
        @Nullable
        private ClientSessionView view;

        @Override
        public void bind(ClientSessionView view) {
            this.view = view;
        }

        @Override
        public void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType currentPhase) {
            if (currentPhase == SpellPhaseType.CASTING) {
                this.createCircle();
            }
        }

        private void createCircle() {
            if (this.circle != null) {
                return;
            }
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) {
                return;
            }

            try {
                int netId = this.v().casterNetId();
                Entity caster = level.getEntity(netId);
                if (caster == null) {
                    return;
                }
                SessionDataStore dataStore = this.v().getDataStore();
                SessionDataValue<Vector3f> value = dataStore.getValue(AllSessionDataKeys.firstPosition);
                if (value == null) {
                    return;
                }
                Vector3f pos = value.value;
                this.circle = ParaComposer.assemble(ExplosionAssets.create());

                this.circle.getTransform().setPosition(pos);
            } catch (Exception e) {
                LOGGER.error("Failed to create {} spell visual: ", ExplosionSpell.SPELL_ID, e);
                return;
            }
            MagicCircleManager.getInstance().addCircle(this.circle);
        }

        @Override
        public void onSpellInterrupted() {
            if (this.circle == null) {
                return;
            }
            MagicCircleManager.getInstance().removeCircle(this.circle);
        }

        @Override
        public void onSpellCompleted() {
            if (this.circle == null) {
                return;
            }
            MagicCircleManager.getInstance().removeCircle(this.circle);
        }

        private ClientSessionView v() {
            if (view == null) {
                throw new NullPointerException("Client session not bound yet");
            }
            return view;
        }
    }
}
