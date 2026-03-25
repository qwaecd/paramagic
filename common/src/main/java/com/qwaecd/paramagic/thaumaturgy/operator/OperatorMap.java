package com.qwaecd.paramagic.thaumaturgy.operator;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.codec.NBTCodec;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatorMap implements IDataSerializable {
    private final Map<String, ParaOpId> data = new HashMap<>();

    public OperatorMap() {
    }

    public void set(OperatorMap other) {
        this.data.clear();
        this.data.putAll(other.data);
    }

    public void clear() {
        this.data.clear();
    }

    @Nonnull
    public List<Entry> getEntriesSnapshot() {
        List<Entry> result = new ArrayList<>(this.data.size());
        for (Map.Entry<String, ParaOpId> entry : this.data.entrySet()) {
            result.add(new Entry(entry.getKey(), entry.getValue()));
        }
        return List.copyOf(result);
    }

    @Nullable
    public ParaOpId get(String path) {
        return this.data.get(path);
    }

    public void put(String path, ParaOpId opId) {
        if (opId == null) {
            return;
        }
        this.data.put(path, opId);
    }

    public void remove(String path) {
        this.data.remove(path);
    }

    @Override
    public void write(DataCodec codec) {
        if (codec instanceof NBTCodec rootNbtCodec) {
            CompoundTag tag = rootNbtCodec.getTag();
            NBTCodec nbtCodec = new NBTCodec(tag);
            for (var entry : this.data.entrySet()) {
                nbtCodec.writeObject(entry.getKey(), entry.getValue());
            }
            return;
        }

        codec.writeInt("size", this.data.size());
        for (var entry : this.data.entrySet()) {
            codec.writeString("path", entry.getKey());
            codec.writeObject("opId", entry.getValue());
        }
    }

    public static OperatorMap fromCodec(DataCodec codec) {
        if (codec instanceof NBTCodec rootNbtCodec) {
            return readFromNBTCodec(rootNbtCodec);
        }
        OperatorMap component = new OperatorMap();
        component.updateFromCodec(codec);
        return component;
    }

    private static OperatorMap readFromNBTCodec(NBTCodec codec) {
        CompoundTag tag = codec.getTag();
        OperatorMap component = new OperatorMap();
        for (String key : tag.getAllKeys()) {
            ParaOpId opId = codec.readObject(key, ParaOpId::fromCodec);
            component.put(key, opId);
        }
        return component;
    }

    public void updateFromCodec(DataCodec codec) {
        if (codec instanceof NBTCodec rootNbtCodec) {
            OperatorMap newComponent = readFromNBTCodec(rootNbtCodec);
            this.set(newComponent);
            return;
        }

        this.data.clear();
        int size = codec.readInt("size");
        for (int i = 0; i < size; i++) {
            String path = codec.readString("path");
            ParaOpId opId = codec.readObject("opId", ParaOpId::fromCodec);
            this.data.put(path, opId);
        }
    }

    public record Entry(@Nonnull String path, @Nonnull ParaOpId opId) {}
}
