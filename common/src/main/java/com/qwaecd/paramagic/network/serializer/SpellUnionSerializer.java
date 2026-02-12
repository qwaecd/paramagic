package com.qwaecd.paramagic.network.serializer;

import com.qwaecd.paramagic.network.codec.PacketByteBufCodec;
import com.qwaecd.paramagic.spell.SpellUnion;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.Optional;

public class SpellUnionSerializer implements EntityDataSerializer<Optional<SpellUnion>> {

    @Override
    public void write(FriendlyByteBuf buffer, Optional<SpellUnion> value) {
        if (value.isEmpty()) {
            // value is empty
            buffer.writeBoolean(false);
            return;
        }
        // contains value
        buffer.writeBoolean(true);
        value.get().write(new PacketByteBufCodec(buffer));
    }

    @Override
    public Optional<SpellUnion> read(FriendlyByteBuf buffer) {
        if (!buffer.readBoolean()) {
            return Optional.empty();
        }
        return Optional.of(SpellUnion.fromCodec(new PacketByteBufCodec(buffer)));
    }

    @Override
    public Optional<SpellUnion> copy(Optional<SpellUnion> value) {
        return value.map(SpellUnion::copy);
    }
}
