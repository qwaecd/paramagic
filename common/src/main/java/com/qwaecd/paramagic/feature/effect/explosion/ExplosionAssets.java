package com.qwaecd.paramagic.feature.effect.explosion;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.spell.config.CircleAssets;

public final class ExplosionAssets {
    public static CircleAssets create() {
        return new CircleAssets(
                new ParaData(ExplosionParaNode.createParaData("under_player")),
                ExplosionParaNode.createAnim("under_player")
        );
    }
}
