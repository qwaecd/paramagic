package com.qwaecd.paramagic.spell.builder;

import com.qwaecd.paramagic.spell.SpellIdentifier;
import com.qwaecd.paramagic.spell.builder.step.*;
import com.qwaecd.paramagic.spell.config.CircleAssets;
import com.qwaecd.paramagic.spell.config.CircleTransformConfig;
import com.qwaecd.paramagic.spell.config.SpellMetaConfig;
import com.qwaecd.paramagic.spell.config.phase.PhaseAssetConfig;
import com.qwaecd.paramagic.spell.config.phase.PhaseConfig;
import com.qwaecd.paramagic.spell.config.phase.PhaseSequenceConfig;
import com.qwaecd.paramagic.spell.core.SpellDefinition;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.view.position.PositionRuleSpec;

import java.util.Objects;

@Deprecated
public class SpellDefBuilder implements MetaStep, PhaseStep {
    private final SpellIdentifier spellId;
    private SpellMetaConfig meta;
    private PhaseSequenceConfig phases;

    private boolean hasPhase = false;

    private SpellDefBuilder(SpellIdentifier spellId) {
        this.spellId = spellId;
    }

    public static MetaStep withSpellId(SpellIdentifier spellId) {
        return new SpellDefBuilder(spellId);
    }

    @Override
    public PhaseStep withMeta(SpellMetaConfig meta) {
        this.meta = Objects.requireNonNull(meta, "meta required");
        return this;
    }

    @Override
    public PhaseStep phase(SpellPhaseType type, float duration) {
        PhaseConfig cfg = new PhaseConfig(type, duration, null);
        addPhase(cfg);
        return this;
    }

    @Override
    public AssetStart phaseWithAssets(SpellPhaseType type, float duration) {
        PhaseConfig base = new PhaseConfig(type, duration, null);
        return new AssetBuilder(this, base);
    }

    @Override
    public SpellDefinition build() {
        Objects.requireNonNull(meta, "meta must be set before build");
        if (!hasPhase){
            throw new IllegalStateException("at least one phase is required");
        }
        return new SpellDefinition(spellId, meta, phases);
    }

    // --- 内部组装 ---
    private void addPhase(PhaseConfig cfg) {
        if (this.phases == null) {
            this.phases = new PhaseSequenceConfig(cfg);
        } else {
            this.phases.addPhaseConfig(cfg);
        }
        this.hasPhase = true;
    }

    private static class AssetBuilder implements AssetStart, PositionRequired, TransformRequired {
        private final SpellDefBuilder parent;
        private final PhaseConfig basePhase;

        private CircleAssets circleAssets;
        private PositionRuleSpec positionRule;
        private CircleTransformConfig transformConfig;

        AssetBuilder(SpellDefBuilder parent, PhaseConfig basePhase) {
            this.parent = parent;
            this.basePhase = basePhase;
        }

        // AssetStart
        @Override
        public AssetStart circleAssets(CircleAssets assets) {
            this.circleAssets = assets;
            return this;
        }

        @Override
        public PositionRequired positionRule(PositionRuleSpec rule) {
            this.positionRule = Objects.requireNonNull(rule, "positionRule required");
            return this;
        }

        // PositionRequired
        @Override
        public TransformRequired transformConfig(CircleTransformConfig cfg) {
            this.transformConfig = Objects.requireNonNull(cfg, "transformConfig required");
            return this;
        }

        // TransformRequired
        @Override
        public PhaseStep endAsset() {
            PhaseAssetConfig assetCfg = new PhaseAssetConfig(
                    circleAssets,
                    Objects.requireNonNull(positionRule, "positionRule required"),
                    Objects.requireNonNull(transformConfig, "transformConfig required")
            );
            PhaseConfig finalCfg = new PhaseConfig(basePhase.getPhaseType(), basePhase.getDuration(), assetCfg);
            parent.addPhase(finalCfg);
            return parent;
        }
    }
}
