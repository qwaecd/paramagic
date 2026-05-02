package com.qwaecd.paramagic.spell.arcane;

import com.qwaecd.paramagic.assembler.AssemblyException;
import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.client.animation.DefaultCircleAnim;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.client.CircleAssets;
import com.qwaecd.paramagic.spell.client.ClientSpellContext;
import com.qwaecd.paramagic.spell.client.SpellPresentation;
import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.spell.util.transform.LambdaFunction;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.UUID;

@PlatformScope(PlatformScopeType.CLIENT)
public class ArcanePresentation implements SpellPresentation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArcanePresentation.class);

    private final MagicCircleHolder holder;
    private boolean finished = false;

    public ArcanePresentation(UUID sessionId, CasterTransformSource casterTransform, CircleAssets assets) {
        this.holder = new MagicCircleHolder(sessionId, assets, casterTransform);
    }

    @Override
    public void onStart(ClientSpellContext context) {
        this.finished = false;
        this.holder.init();
    }

    @Override
    public void tick(ClientSpellContext context) {
    }

    @Override
    public void onStop(ClientSpellContext context, EndSpellReason reason) {
        this.holder.close();
        this.finished = true;
    }

    @Override
    public boolean canDispose() {
        return this.finished;
    }

    @Override
    public void dispose(ClientSpellContext context) {
        this.holder.close();
        this.finished = true;
    }

    static class MagicCircleHolder {
        @Nonnull
        final MagicCircle circle;
        private boolean closed = false;

        public MagicCircleHolder(UUID sessionId, CircleAssets assets, CasterTransformSource source) {
            MagicCircle magicCircle;
            try {
                magicCircle = ParaComposer.assemble(assets);
            } catch (AssemblyException e) {
                magicCircle = new MagicCircle();
                LOGGER.warn("Failed to assemble MagicCircle for ArcanePresentation. Session ID: {}, Error: {}", sessionId, e.getMessage());
            }

            if (assets.getAnimBindingConfig() == null) {
                magicCircle.getTransform().setScale(0.0f);
                DefaultCircleAnim defaultCircleAnim = new DefaultCircleAnim(magicCircle, new Vector3f(1.0f, 1.0f, 1.0f), 0.7f);
                defaultCircleAnim.setRotationSpeed((float) Math.toRadians(-45.0f));
                magicCircle.setAnimator(defaultCircleAnim);
            }

            // lambda 简直是世界上最伟大的发明喵
            LambdaFunction lambdaFunction = new LambdaFunction((item, caster, tempSample) -> {
                caster.applyTo(tempSample);
                Vector3f position = tempSample.position;
                item.setPosition(position.x, position.y + 0.01f, position.z);
            });
            magicCircle.registerModifyTransform(transform -> lambdaFunction.apply(transform, source));

            this.circle = magicCircle;
        }

        public void init() {
            MagicCircleManager.getInstance().addCircle(this.circle);
        }

        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.circle.requestDestroy();
        }
    }
}
