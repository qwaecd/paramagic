package com.qwaecd.paramagic.spell;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;

public record SpellIdentifier(String id) implements IDataSerializable {
    public SpellIdentifier(String modId, String spellName) {
        this(modId + ":" + spellName);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeString("id", this.id);
    }

    public static SpellIdentifier fromCodec(DataCodec codec) {
        String id = codec.readString("id");
        return new SpellIdentifier(id);
    }

    public SpellIdentifier copy() {
        return new SpellIdentifier(this.id);
    }
}
