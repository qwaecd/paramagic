package com.qwaecd.paramagic.network.serializer;

import com.qwaecd.paramagic.spell.Spell;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.Optional;

public final class AllEntityDataSerializers {
    public static final EntityDataSerializer<Optional<Spell>> OPTIONAL_SPELL = new OptionalSpellSerializer();
}
