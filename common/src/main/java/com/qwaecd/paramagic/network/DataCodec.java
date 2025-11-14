package com.qwaecd.paramagic.network;

import java.util.UUID;
import java.util.function.Function;

public abstract class DataCodec {
    public abstract void writeInt(String key, int value);
    public abstract void writeString(String key, String value);
    public abstract void writeUUID(String key, UUID value);
    public abstract void writeFloat(String key, float value);
    public abstract void writeBoolean(String key, boolean value);
    public abstract void writeFloatArray(String key, float[] values);
    public abstract void writeIntArray(String key, int[] values);
    public abstract <T extends IDataSerializable> void writeObject(String key, T object);


    public abstract int readInt(String key);
    public abstract String readString(String key);
    public abstract UUID readUUID(String key);
    public abstract float readFloat(String key);
    public abstract boolean readBoolean(String key);
    public abstract float[] readFloatArray(String key);
    public abstract int[] readIntArray(String key);
    /**
     * 从编码器中读取一个子对象。
     * @param key 对象的键
     * @param factory 一个函数，它接收一个子编码器，并返回一个完整的对象。
     * @param <T> 对象的类型
     * @return 读取并创建的完整对象
     */
    public abstract <T extends IDataSerializable> T readObject(String key, Function<DataCodec, T> factory);
}
