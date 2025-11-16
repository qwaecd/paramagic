package com.qwaecd.paramagic.network.serializer;

import com.qwaecd.paramagic.network.codec.PacketByteBufCodec;
import com.qwaecd.paramagic.spell.Spell;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.Optional;

public class OptionalSpellSerializer implements EntityDataSerializer<Optional<Spell>> {
    @Override
    public void write(FriendlyByteBuf buffer, Optional<Spell> value) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        if (value.isEmpty()) {
            codec.writeBoolean("hasSpell", false);
            return;
        }
        codec.writeBoolean("hasSpell", true);
        codec.writeObject("spell", value.get());
    }

    @Override
    public Optional<Spell> read(FriendlyByteBuf buffer) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        if (!codec.readBoolean("hasSpell")) {
            return Optional.empty();
        }
        Spell spell = codec.readObject("spell", Spell::fromCodec);
        return Optional.of(spell);
    }

    @Override
    public Optional<Spell> copy(Optional<Spell> value) {
        return value.map(Spell::copy);
    }
}
