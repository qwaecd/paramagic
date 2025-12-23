package com.qwaecd.paramagic.network;

import org.joml.Vector3f;

import javax.annotation.Nullable;
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
    public abstract <T extends IDataSerializable> void writeObjectArray(String key, T[] object);
    public void writeVector3f(String key, Vector3f v) {
        this.writeFloatArray(key, new float[]{v.x, v.y, v.z});
    }
    public abstract <T extends IDataSerializable> void writeObjectNullable(String key, @Nullable T object);
    public abstract void writeStringNullable(String key, @Nullable String value);

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
    public abstract <T extends IDataSerializable> IDataSerializable[] readObjectArray(String key, Function<DataCodec, T> factory);
    public Vector3f readVector3f(String key) {
        float[] arr = this.readFloatArray(key);
        return new Vector3f(arr[0], arr[1], arr[2]);
    }

    /**
     * 从编码器中读取一个可为空的子对象。
     * @param factory 仅处理非空子编码器的函数
     */
    @Nullable
    public abstract <T extends IDataSerializable> T readObjectNullable(String key, Function<DataCodec, T> factory);
    public abstract @Nullable String readStringNullable(String key);
}
