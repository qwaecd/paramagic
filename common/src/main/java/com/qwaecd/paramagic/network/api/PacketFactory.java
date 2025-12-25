package com.qwaecd.paramagic.network.api;

import com.qwaecd.paramagic.network.DataCodec;

public interface PacketFactory<T> {
    T decode(DataCodec codec);
}
