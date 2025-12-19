package com.qwaecd.paramagic.spell.config;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;

public class SpellMetaConfig implements IDataSerializable {
    public SpellMetaConfig() {
    }

    @Override
    public void write(DataCodec codec) {
    }

    public static SpellMetaConfig fromCodec(DataCodec dataCodec) {
        return new SpellMetaConfig();
    }
}
