package com.qwaecd.paramagic.network;

public interface IDataSerializable {
    /**
     * 将对象数据写入编码器
     * @param codec 具体的编码器实例
     */
    void write(DataCodec codec);
}
