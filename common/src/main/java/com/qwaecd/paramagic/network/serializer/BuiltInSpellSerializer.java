package com.qwaecd.paramagic.network.serializer;

import com.qwaecd.paramagic.network.codec.PacketByteBufCodec;
import com.qwaecd.paramagic.spell.SpellIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.Optional;

public class BuiltInSpellSerializer implements EntityDataSerializer<Optional<SpellIdentifier>> {
    @Override
    public void write(FriendlyByteBuf buffer, Optional<SpellIdentifier> value) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        if (value.isEmpty()) {
            codec.writeBoolean("hasSpell", false);
            return;
        }
        codec.writeBoolean("hasSpell", true);
        codec.writeObject("spellId", value.get());
    }

    @Override
    public Optional<SpellIdentifier> read(FriendlyByteBuf buffer) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        if (!codec.readBoolean("hasSpell")) {
            return Optional.empty();
        }
        SpellIdentifier spellId = codec.readObject("spellId", SpellIdentifier::fromCodec);
        return Optional.of(spellId);
    }

    @Override
    public Optional<SpellIdentifier> copy(Optional<SpellIdentifier> value) {
        return value.map(SpellIdentifier::copy);
    }
}
