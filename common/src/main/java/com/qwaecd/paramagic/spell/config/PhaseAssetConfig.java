package com.qwaecd.paramagic.spell.config;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.spell.view.position.CirclePositionRule;

import javax.annotation.Nullable;

@SuppressWarnings({"LombokGetterMayBeUsed", "ClassCanBeRecord"})
public class PhaseAssetConfig implements IDataSerializable {
    @Nullable
    private final CircleAssets circleAssets;

    private final CirclePositionRule positionRule;

    private final CircleTransformConfig transformConfig;

    public PhaseAssetConfig(
            @Nullable CircleAssets circleAssets,
            CirclePositionRule positionRule,
            CircleTransformConfig transformConfig
    ) {
        this.circleAssets = circleAssets;
        this.positionRule = positionRule;
        this.transformConfig = transformConfig;
    }

    @Nullable
    public CircleAssets getSpellAssets() {
        return this.circleAssets;
    }

    public CirclePositionRule getPositionRule() {
        return this.positionRule;
    }

    public CircleTransformConfig getTransformConfig() {
        return this.transformConfig;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeObjectNullable("assets", this.circleAssets);
        codec.writeObject("positionRule", this.positionRule);
        codec.writeObject("transformConfig", this.transformConfig);
    }

    public static PhaseAssetConfig fromCodec(DataCodec codec) {
        CircleAssets assets = codec.readObjectNullable("assets", CircleAssets::fromCodec);
        CirclePositionRule rule = codec.readObject("positionRule", CirclePositionRule::fromCodec);
        CircleTransformConfig config = codec.readObject("transformConfig", CircleTransformConfig::fromCodec);
        return new PhaseAssetConfig(assets, rule, config);
    }
}
