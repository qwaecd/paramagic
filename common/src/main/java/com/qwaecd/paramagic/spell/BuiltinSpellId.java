package com.qwaecd.paramagic.spell;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;

public record BuiltinSpellId(String id) implements IDataSerializable {
    public BuiltinSpellId(String modId, String spellName) {
        this(modId + ":" + spellName);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeString("id", this.id);
    }

    public static BuiltinSpellId fromCodec(DataCodec codec) {
        String id = codec.readString("id");
        return new BuiltinSpellId(id);
    }

    public BuiltinSpellId copy() {
        return new BuiltinSpellId(this.id);
    }
}
