package com.qwaecd.paramagic.spell.session.store;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.codec.codable.CodableTypeRegistry;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

@SuppressWarnings("ClassCanBeRecord")
public class SessionDataValue<T> implements IDataSerializable {
    @Nonnull
    public final T value;

    public SessionDataValue(@Nonnull T value) {
        this.value = value;
    }

    @Override
    public void write(DataCodec codec) {
        int typeId = CodableTypeRegistry.getTypeId(value.getClass());
        codec.writeInt("typeId", typeId);
        CodableTypeRegistry.<T>getSerializer(typeId).write(codec, "value", this.value);
    }

    @SuppressWarnings("unchecked")
    public static <T> SessionDataValue<T> fromCodec(DataCodec codec) {
        int typeId = codec.readInt("typeId");
        BiFunction<DataCodec, String, ?> deserializer = CodableTypeRegistry.getDeserializer(typeId);

        Object value = deserializer.apply(codec, "value");

        SessionDataKey<?> byId = AllSessionDataKeys.getById(typeId);
        if (byId == null) {
            throw new NullPointerException("No SessionData registered for id: " + typeId);
        }

        Class<?> valueType = byId.typeClass;
        if (!valueType.isInstance(value)) {
            throw new ClassCastException("Value type mismatch for SessionData id '" + typeId + "': expected " + valueType.getName() + ", got " + value.getClass().getName());
        }

        return new SessionDataValue<>((T) value);
    }
}
