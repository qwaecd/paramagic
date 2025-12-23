package com.qwaecd.paramagic.spell.logic;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;


public class SpellLogic implements IDataSerializable {

    public SpellLogic() {
    }

    @Override
    public void write(DataCodec codec) {
    }

    public static SpellLogic fromCodec(DataCodec codec) {
        return new SpellLogic();
    }
}
