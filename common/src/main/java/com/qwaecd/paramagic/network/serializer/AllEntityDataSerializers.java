package com.qwaecd.paramagic.network.serializer;

import com.qwaecd.paramagic.spell.SpellIdentifier;
import com.qwaecd.paramagic.spell.core.SpellDefinition;
import com.qwaecd.paramagic.spell.session.SpellSessionRef;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class AllEntityDataSerializers {
    private static final List<EntityDataSerializer<?>> SERIALIZER_LIST = new ArrayList<>();
    public static final EntityDataSerializer<Optional<SpellDefinition>> OPTIONAL_SPELL_DEF = register(new OptionalSpellDefSerializer());
    public static final EntityDataSerializer<Optional<SpellSessionRef>> SPELL_SESSION_REF = register(new SpellSessionRefSerializer());
    public static final EntityDataSerializer<Optional<SpellIdentifier>> BUILT_IN_SPELL = register(new BuiltInSpellSerializer());
    private static <T> EntityDataSerializer<T> register(EntityDataSerializer<T> serializer) {
        SERIALIZER_LIST.add(serializer);
        return serializer;
    }

    public static void forEachSerializer(Consumer<EntityDataSerializer<?>> consumer) {
        for (EntityDataSerializer<?> entityDataSerializer : SERIALIZER_LIST) {
            consumer.accept(entityDataSerializer);
        }
    }
}
