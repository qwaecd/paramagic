package com.qwaecd.paramagic.network.codec.codable;

import com.qwaecd.paramagic.network.DataCodec;

import java.util.function.BiFunction;

public interface ICodableType<T> {
    int getTypeId();
    Class<T> getTypeClass();
    WriteConsumer<T> serializer();
    BiFunction<DataCodec, String, T> deserializer();
}
