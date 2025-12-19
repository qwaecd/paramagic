package com.qwaecd.paramagic.network.serializer;

import com.qwaecd.paramagic.network.codec.PacketByteBufCodec;
import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.core.SpellDefinition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.Optional;

public class OptionalSpellDefSerializer implements EntityDataSerializer<Optional<SpellDefinition>> {
    @Override
    public void write(FriendlyByteBuf buffer, Optional<SpellDefinition> value) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        if (value.isEmpty()) {
            codec.writeBoolean("hasSpell", false);
            return;
        }
        codec.writeBoolean("hasSpell", true);
        codec.writeObject("spell", value.get());
    }

    @Override
    public Optional<SpellDefinition> read(FriendlyByteBuf buffer) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        if (!codec.readBoolean("hasSpell")) {
            return Optional.empty();
        }
        SpellDefinition spell = codec.readObject("spell", SpellDefinition::fromCodec);
        return Optional.of(spell);
    }

    @Override
    public Optional<SpellDefinition> copy(Optional<SpellDefinition> value) {
        return value.map(SpellDefinition::copy);
    }
}
