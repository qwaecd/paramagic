package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.assembler.AssemblyException;
import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.client.animation.DefaultCircleAnim;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.spell.config.CircleAssets;
import com.qwaecd.paramagic.spell.session.SessionState;
import com.qwaecd.paramagic.spell.util.transform.LambdaFunction;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import com.qwaecd.paramagic.spell.view.HybridCasterSource;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ArcSessionClient extends ClientSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArcSessionClient.class);

    @Nonnull
    private final MagicCircleHolder circleHolder;

    public ArcSessionClient(UUID sessionId, @Nonnull HybridCasterSource casterSource, CircleAssets assets) {
        super(sessionId, casterSource);
        this.circleHolder = new MagicCircleHolder(sessionId, assets, this.casterSource());
    }

    @Override
    public void tick(float deltaTime) {
    }

    public void init() {
        this.circleHolder.init();
    }

    @Override
    public boolean canRemoveFromManager() {
        return !isState(SessionState.RUNNING);
    }

    @Override
    public void close() {
        super.close();
        this.circleHolder.close();
    }

    public static class MagicCircleHolder {
        @Nonnull
        final MagicCircle circle;
        public MagicCircleHolder(UUID sessionId, CircleAssets assets, CasterTransformSource source) {
            MagicCircle magicCircle;
            try {
                magicCircle = ParaComposer.assemble(assets);
            } catch (AssemblyException e) {
                magicCircle = new MagicCircle();
                LOGGER.warn("Failed to assemble MagicCircle for ArcSessionClient. Session ID: {}, Error: {}", sessionId, e.getMessage());
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
            MagicCircleManager.getInstance().removeCircle(this.circle);
        }
    }
}
