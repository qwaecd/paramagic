package com.qwaecd.paramagic.spell.struct.phase;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.spell.struct.SpellAssets;
import com.qwaecd.paramagic.spell.struct.position.CirclePositionRule;

import javax.annotation.Nullable;

@SuppressWarnings({"LombokGetterMayBeUsed", "ClassCanBeRecord"})
public class PhaseAssetConfig implements IDataSerializable {
    @Nullable
    private final SpellAssets spellAssets;

    private final CirclePositionRule positionRule;

    private final CircleTransformConfig transformConfig;

    public PhaseAssetConfig(
            @Nullable SpellAssets spellAssets,
            CirclePositionRule positionRule,
            CircleTransformConfig transformConfig
    ) {
        this.spellAssets = spellAssets;
        this.positionRule = positionRule;
        this.transformConfig = transformConfig;
    }

    @Nullable
    public SpellAssets getSpellAssets() {
        return this.spellAssets;
    }

    public CirclePositionRule getPositionRule() {
        return this.positionRule;
    }

    public CircleTransformConfig getTransformConfig() {
        return this.transformConfig;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeObjectNullable("assets", this.spellAssets);
        codec.writeObject("positionRule", this.positionRule);
        codec.writeObject("transformConfig", this.transformConfig);
    }

    public static PhaseAssetConfig fromCodec(DataCodec codec) {
        SpellAssets assets = codec.readObjectNullable("assets", SpellAssets::fromCodec);
        CirclePositionRule rule = codec.readObject("positionRule", CirclePositionRule::fromCodec);
        CircleTransformConfig config = codec.readObject("transformConfig", CircleTransformConfig::fromCodec);
        return new PhaseAssetConfig(assets, rule, config);
    }
}
