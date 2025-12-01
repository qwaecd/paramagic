package com.qwaecd.paramagic.feature.effect.exposion;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.spell.struct.SpellAssets;

public final class ExplosionAssets {
    public static SpellAssets create() {
        return new SpellAssets(
                new ParaData(ExplosionParaNode.createParaData("under_player")),
                ExplosionParaNode.createAnim("under_player")
        );
    }
}
