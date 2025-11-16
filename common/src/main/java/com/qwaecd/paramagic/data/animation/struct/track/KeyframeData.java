package com.qwaecd.paramagic.data.animation.struct.track;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.codec.codable.CodableTypeRegistry;

import java.util.function.BiFunction;

public record KeyframeData<T>(float time, T value, String interpolation) implements IDataSerializable {

    public KeyframeData(float time, T value) {
        this(time, value, "linear");
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeFloat("time", this.time);

        int typeId = CodableTypeRegistry.getTypeId(value.getClass());
        codec.writeInt("typeId", typeId);
        CodableTypeRegistry.<T>getSerializer(typeId).write(codec, "value", this.value);

        codec.writeString("interpolation", this.interpolation);
    }

    public static KeyframeData<?> fromCodec(DataCodec codec) {
        float time = codec.readFloat("time");
        int typeId = codec.readInt("typeId");
        BiFunction<DataCodec, String, ?> deserializer = CodableTypeRegistry.getDeserializer(typeId);

        Object value = deserializer.apply(codec, "value");

        String interpolation = codec.readString("interpolation");
        return new KeyframeData<>(time, value, interpolation);
    }
}
