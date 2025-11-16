package com.qwaecd.paramagic.network.codec;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;
import java.util.function.Function;

public class PacketByteBufCodec extends DataCodec {
    private final FriendlyByteBuf buf;

    public PacketByteBufCodec(FriendlyByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public void writeInt(String key, int value) {
        this.buf.writeInt(value);
    }

    @Override
    public void writeString(String key, String value) {
        this.buf.writeUtf(value);
    }

    @Override
    public void writeUUID(String key, UUID value) {
        this.buf.writeUUID(value);
    }

    @Override
    public void writeFloat(String key, float value) {
        this.buf.writeFloat(value);
    }

    @Override
    public void writeBoolean(String key, boolean value) {
        this.buf.writeBoolean(value);
    }

    @Override
    public void writeFloatArray(String key, float[] values) {
        this.buf.writeInt(values.length);
        for (float v : values) {
            this.buf.writeFloat(v);
        }
    }

    @Override
    public void writeIntArray(String key, int[] values) {
        this.buf.writeInt(values.length);
        for (int v : values) {
            this.buf.writeInt(v);
        }
    }

    @Override
    public <T extends IDataSerializable> void writeObject(String key, T object) {
        object.write(this);
    }

    @Override
    public int readInt(String key) {
        return this.buf.readInt();
    }

    @Override
    public String readString(String key) {
        return this.buf.readUtf();
    }

    @Override
    public UUID readUUID(String key) {
        return this.buf.readUUID();
    }

    @Override
    public float readFloat(String key) {
        return this.buf.readFloat();
    }

    @Override
    public boolean readBoolean(String key) {
        return this.buf.readBoolean();
    }

    @Override
    public float[] readFloatArray(String key) {
        final int length = this.buf.readInt();
        float[] floats = new float[length];
        for (int j = 0; j < length; j++) {
            floats[j] = this.buf.readFloat();
        }
        return floats;
    }

    @Override
    public int[] readIntArray(String key) {
        final int length = this.buf.readInt();
        int[] ints = new int[length];
        for (int j = 0; j < length; j++) {
            ints[j] = this.buf.readInt();
        }
        return ints;
    }

    @Override
    public <T extends IDataSerializable> T readObject(String key, Function<DataCodec, T> factory) {
        return factory.apply(this);
    }
}
