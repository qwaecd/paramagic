package com.qwaecd.paramagic.spell.listener;

import com.qwaecd.paramagic.assembler.AssemblyException;
import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.core.render.TransformSample;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.config.CircleTransformConfig;
import com.qwaecd.paramagic.spell.config.phase.PhaseAssetConfig;
import com.qwaecd.paramagic.spell.config.phase.PhaseConfig;
import com.qwaecd.paramagic.spell.core.SpellDefinition;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.session.client.ClientSessionListener;
import com.qwaecd.paramagic.spell.session.client.ClientSessionView;
import com.qwaecd.paramagic.spell.view.PositionRuleRegistry;
import com.qwaecd.paramagic.spell.view.position.PositionRule;
import com.qwaecd.paramagic.spell.view.position.PositionRuleContext;
import com.qwaecd.paramagic.spell.view.position.PositionRuleSpec;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

@PlatformScope(PlatformScopeType.CLIENT)
public class MultiPhaseRenderListener implements ClientSessionListener {
    private static final Logger logger = LoggerFactory.getLogger(MultiPhaseRenderListener.class);
    private final SpellDefinition spellDefinition;

    @Nullable
    private ClientSessionView view;
    private final Map<SpellPhaseType, MagicCircle> activeCircles = new EnumMap<>(SpellPhaseType.class);

    private final TransformSample tmpSample = new TransformSample();

    public MultiPhaseRenderListener(SpellDefinition spellDefinition) {
        this.spellDefinition = spellDefinition;
    }

    private void createPhaseCircle(SpellPhaseType phaseType) {
        PhaseConfig phaseConfig = this.spellDefinition.phases.getPhaseConfig(phaseType);

        PhaseAssetConfig assetConfig = phaseConfig.getAssetConfig();
        if (assetConfig == null || assetConfig.getSpellAssets() == null) {
            // 本阶段没有需要渲染的魔法阵
            return;
        }

        try {
            // 从给定的资源配置组装魔法阵
            MagicCircle circle = ParaComposer.assemble(assetConfig.getSpellAssets());

            // 从给定位置规则创建规则实例
            PositionRuleSpec positionRuleSpec = assetConfig.getPositionRule();
            PositionRule positionRule = PositionRuleRegistry.create(positionRuleSpec);

            // 应用初始变换
            flushTFSource();
            PositionRuleContext positionContext = new PositionRuleContext(circle, this.tmpSample, positionRuleSpec);
            positionRule.onAttach(positionContext);
            positionRule.apply(positionContext);

            CircleTransformConfig transformConfig = assetConfig.getTransformConfig();
            Vector3f scale = transformConfig.getInitialScale();
            Vector3f rotation = transformConfig.getInitialRotation();
            circle.getTransform()
                    .setScale(scale)
                    .setRotationRadians(rotation.x, rotation.y, rotation.z);

            // 注册位置规则回调器
            this.registerModifier(circle, positionRule, positionContext);

            // 提交 MagicCircle
            MagicCircleManager.getInstance().addCircle(circle);
            activeCircles.put(phaseType, circle);
        } catch (AssemblyException e) {
            logger.warn("Failed to assemble spell assets for phase {}", phaseType, e);
        }
    }

    @Override
    public void bind(ClientSessionView view) {
        this.view = view;
    }

    @Override
    public void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType currentPhase) {
        createPhaseCircle(currentPhase);
    }

    @Override
    public void onSpellInterrupted() {
        this.cleanupAllCircles();
    }

    @Override
    public void onSpellCompleted() {
        this.cleanupAllCircles();
    }

    @Override
    public void onSessionClose() {
        if (!this.activeCircles.isEmpty())
            this.cleanupAllCircles();
    }

    private void cleanupPhaseCircle(SpellPhaseType phaseType) {
        MagicCircle circle = this.activeCircles.remove(phaseType);
        if (circle != null) {
            MagicCircleManager.getInstance().removeCircle(circle);
        }
    }

    private void cleanupAllCircles() {
        MagicCircleManager.getInstance().removeCircle(this.activeCircles.values());
        this.activeCircles.clear();
    }

    private ClientSessionView v() {
        if (view == null) {
            throw new NullPointerException("Client session not bound yet");
        }
        return view;
    }

    private void registerModifier(
            @Nonnull MagicCircle circle,
            @Nonnull PositionRule positionRule,
            @Nonnull PositionRuleContext context
    ) {
        if (!positionRule.needsContinuousUpdate()) {
            return;
        }
        circle.registerModifyTransform(transform -> {
            this.flushTFSource();
            context.setCasterTransform(this.tmpSample);
            positionRule.apply(context);
        });
    }

    private void flushTFSource() {
        this.v().casterSource().applyTo(this.tmpSample);
    }
}
