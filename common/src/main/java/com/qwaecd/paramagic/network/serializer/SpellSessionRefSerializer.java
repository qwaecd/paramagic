package com.qwaecd.paramagic.network.serializer;

import com.qwaecd.paramagic.spell.session.SpellSessionRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.Optional;


public class SpellSessionRefSerializer implements EntityDataSerializer<Optional<SpellSessionRef>> {
    @Override
    public void write(FriendlyByteBuf buffer, Optional<SpellSessionRef> value) {
        if (value.isEmpty()) {
            // ref is empty
            buffer.writeBoolean(false);
            return;
        }
        // contains ref
        buffer.writeBoolean(true);
        value.get().write(buffer);
    }

    @Override
    public Optional<SpellSessionRef> read(FriendlyByteBuf buffer) {
        if (!buffer.readBoolean()) {
            return Optional.empty();
        }
        return Optional.of(SpellSessionRef.fromBuffer(buffer));
    }

    @Override
    public Optional<SpellSessionRef> copy(Optional<SpellSessionRef> value) {
        return value.map(SpellSessionRef::copy);
    }
}
