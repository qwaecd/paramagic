package com.qwaecd.paramagic.network.particle.emitter;

import com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties;
import com.qwaecd.paramagic.core.particle.emitter.property.key.PropertyKey;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.codec.codable.CodableTypeRegistry;

import java.util.function.BiFunction;

public class EmitterPropertyValue<T> implements IDataSerializable {
    private final PropertyKey<T> propertyKey;
    private final T value;

    public EmitterPropertyValue(PropertyKey<T> propertyKey, T value) {
        this.propertyKey = propertyKey;
        this.value = value;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeString("propertyName", this.propertyKey.getName());
        int typeId = CodableTypeRegistry.getTypeId(this.propertyKey.getValueType());
        codec.writeInt("valueType", typeId);
        CodableTypeRegistry.<T>getSerializer(typeId).write(codec, "value", this.value);
    }

    @SuppressWarnings("unchecked")
    public static <T> EmitterPropertyValue<T> fromCodec(DataCodec codec) {
        String propertyName = codec.readString("propertyName");
        int typeId = codec.readInt("valueType");
        BiFunction<DataCodec, String, ?> deserializer = CodableTypeRegistry.getDeserializer(typeId);
        Object value = deserializer.apply(codec, "value");
        PropertyKey<?> propertyKey = AllEmitterProperties.get(propertyName);
        if (propertyKey == null) {
            throw new NullPointerException("Unknown emitter property key: " + propertyName);
        }
        Class<?> valueType = propertyKey.getValueType();
        if (!valueType.isInstance(value)) {
            throw new ClassCastException("Value type mismatch for property '" + propertyName + "': expected " + valueType.getName() + ", got " + value.getClass().getName());
        }
        return new EmitterPropertyValue<>((PropertyKey<T>) propertyKey, (T) value);
    }
}
