package com.qwaecd.paramagic.network.codec;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Function;

public class NBTCodec extends DataCodec {
    @Nonnull
    private final CompoundTag tag;

    public NBTCodec(@Nonnull CompoundTag tag) {
        this.tag = tag;
    }

    @Nonnull
    public CompoundTag getTag() {
        return tag;
    }

    @Override
    public void writeInt(String key, int value) {
        this.tag.putInt(key, value);
    }

    @Override
    public void writeString(String key, String value) {
        this.tag.putString(key, value);
    }

    @Override
    public void writeUUID(String key, UUID value) {
        this.tag.putUUID(key, value);
    }

    @Override
    public void writeFloat(String key, float value) {
        this.tag.putFloat(key, value);
    }

    @Override
    public void writeBoolean(String key, boolean value) {
        this.tag.putBoolean(key, value);
    }

    @Override
    public void writeFloatArray(String key, float[] values) {
        this.tag.putIntArray(key, toIntArray(values));
    }

    private static int[] toIntArray(float[] floatArray) {
        int[] intArray = new int[floatArray.length];
        for (int i = 0; i < floatArray.length; i++) {
            intArray[i] = Float.floatToIntBits(floatArray[i]);
        }
        return intArray;
    }

    @Override
    public void writeIntArray(String key, int[] values) {
        this.tag.putIntArray(key, values);
    }

    @Override
    public void writeLong(String key, long value) {
        this.tag.putLong(key, value);
    }

    @Override
    public <T extends IDataSerializable> void writeObject(String key, T object) {
        CompoundTag objTag = new CompoundTag();
        object.write(new NBTCodec(objTag));
        this.tag.put(key, objTag);
    }

    @Override
    public <T extends IDataSerializable> void writeObjectArray(String key, T[] object) {
        CompoundTag objArrayTag = new CompoundTag();
        objArrayTag.putInt("length", object.length);
        for (int i = 0; i < object.length; i++) {
            this.writeObject("obj" + i, object[i]);
        }
        this.tag.put(key, objArrayTag);
    }

    @Override
    public <T extends IDataSerializable> void writeObjectNullable(String key, @Nullable T object) {
        if (object != null) {
            CompoundTag objTag = new CompoundTag();
            DataCodec objCodec = new NBTCodec(objTag);
            object.write(objCodec);
            this.tag.put(key, objTag);
        }
        // else do nothing
    }

    @Override
    public void writeStringNullable(String key, @Nullable String value) {
        if (value != null) {
            this.tag.putString(key, value);
        }
    }

    @Override
    public int readInt(String key) {
        return this.tag.getInt(key);
    }

    @Override
    public String readString(String key) {
        return this.tag.getString(key);
    }

    @Override
    public UUID readUUID(String key) {
        return this.tag.getUUID(key);
    }

    @Override
    public float readFloat(String key) {
        return this.tag.getFloat(key);
    }

    @Override
    public boolean readBoolean(String key) {
        return this.tag.getBoolean(key);
    }

    @Override
    public float[] readFloatArray(String key) {
        int[] intArray = this.tag.getIntArray(key);
        return toFloatArray(intArray);
    }

    private static float[] toFloatArray(int[] intArray) {
        float[] floatArray = new float[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            floatArray[i] = Float.intBitsToFloat(intArray[i]);
        }
        return floatArray;
    }

    @Override
    public int[] readIntArray(String key) {
        return this.tag.getIntArray(key);
    }

    @Override
    public long readLong(String key) {
        return this.tag.getLong(key);
    }

    @Override
    public <T extends IDataSerializable> T readObject(String key, Function<DataCodec, T> factory) {
        CompoundTag objTag = this.tag.getCompound(key);
        return factory.apply(new NBTCodec(objTag));
    }

    @Override
    public <T extends IDataSerializable> IDataSerializable[] readObjectArray(String key, Function<DataCodec, T> factory) {
        final int length = this.tag.getCompound(key).getInt("length");
        @SuppressWarnings("unchecked")
        T[] array = (T[]) new IDataSerializable[length];
        for (int i = 0; i < length; i++) {
            array[i] = this.readObject("obj" + i, factory);
        }
        return array;
    }

    @Override
    @Nullable
    public <T extends IDataSerializable> T readObjectNullable(String key, Function<DataCodec, T> factory) {
        if (!this.tag.contains(key)) {
            return null;
        }
        CompoundTag objTag = this.tag.getCompound(key);
        return factory.apply(new NBTCodec(objTag));
    }

    @Override
    @Nullable
    public String readStringNullable(String key) {
        if (!this.tag.contains(key)) {
            return null;
        }
        return this.tag.getString(key);
    }
}
