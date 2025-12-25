package com.qwaecd.paramagic.network.api;

import com.qwaecd.paramagic.network.DataCodec;


public interface Packet<T extends Packet<T>> {
    /**
     * 将该数据包写入 codec.
     */
    void encode(DataCodec codec);
    Class<T> getPacketClass();
    PacketIdentifier getIdentifier();
}
