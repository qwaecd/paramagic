package com.qwaecd.paramagic.network.codec.codable;

import com.qwaecd.paramagic.network.DataCodec;

@FunctionalInterface
public interface WriteConsumer<T> {
    void write(DataCodec codec, String key, T value);
}
