package com.qwaecd.paramagic.spell.session.store;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@SuppressWarnings("LombokGetterMayBeUsed")
public class SessionDataStore implements IDataSerializable {
    private final Map<Integer, DataValue<?>> dataMap = new ConcurrentHashMap<>();

    @SuppressWarnings("ClassCanBeRecord")
    public static class DataValue<T> implements IDataSerializable {
        @Getter
        final int sequenceNumber;
        @Nonnull
        final SessionDataValue<T> value;

        DataValue(int sequenceNumber, @Nonnull SessionDataValue<T> value) {
            this.sequenceNumber = sequenceNumber;
            this.value = value;
        }

        public SessionDataValue<T> get() {
            return this.value;
        }

        @Override
        public void write(DataCodec codec) {
            codec.writeInt("sequenceNumber", this.sequenceNumber);
            codec.writeObject("value", this.value);
        }

        public static <T> DataValue<T> fromCodec(DataCodec codec) {
            int sequenceNumber = codec.readInt("sequenceNumber");
            SessionDataValue<T> value = codec.readObject("value", SessionDataValue::fromCodec);
            return new DataValue<>(sequenceNumber, value);
        }
    }

    private boolean isDirty = false;

    public void setValue(int dataId, @Nonnull SessionDataValue<?> value) {
        DataValue<?> originValue = this.dataMap.get(dataId);
        int sequenceNumber = originValue == null ? 0 : originValue.sequenceNumber + 1;
        this.dataMap.put(dataId, new DataValue<>(sequenceNumber, value));
        this.isDirty = true;
    }

    public void setValue(SessionDataKey<?> sessionDataKey, @Nonnull SessionDataValue<?> value) {
        this.setValue(sessionDataKey.id, value);
    }

    @Nullable
    public SessionDataValue<?> getValue(int dataId) {
        DataValue<?> dataValue = this.dataMap.get(dataId);
        if (dataValue == null) {
            return null;
        }
        return dataValue.value;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> SessionDataValue<T> getValue(SessionDataKey<T> sessionDataKey) {
        return (SessionDataValue<T>) this.getValue(sessionDataKey.id);
    }

    @Nullable
    public DataValue<?> getDataValue(int dataId) {
        return this.dataMap.get(dataId);
    }

    @Nullable
    public SessionDataValue<?> removeValue(int dataId) {
        DataValue<?> removed = this.dataMap.remove(dataId);
        if (removed != null) {
            this.isDirty = true;
            return removed.value;
        }
        return null;
    }

    public void forEachEntry(BiConsumer<Integer, DataValue<?>> action) {
        this.dataMap.forEach(action);
    }

    public SessionDataValue<?> removeValue(SessionDataKey<?> sessionDataKey) {
        return this.removeValue(sessionDataKey.id);
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setDirty() {
        this.isDirty = true;
    }

    public void setDirty(boolean b) {
        this.isDirty = b;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("size", dataMap.size());
        for (var entry : dataMap.entrySet()) {
            int dataId = entry.getKey();
            DataValue<?> value = entry.getValue();
            codec.writeInt("dataId", dataId);
            codec.writeObject("value", value);
        }
    }

    public static SessionDataStore fromCodec(DataCodec codec) {
        SessionDataStore store = new SessionDataStore();
        int size = codec.readInt("size");
        for (int i = 0; i < size; i++) {
            int dataId = codec.readInt("dataId");
            DataValue<?> value = codec.readObject("value", DataValue::fromCodec);
            store.setValue(dataId, value);
        }
        return store;
    }

    public void setDataValueWithSeq(int dataId, int sequenceNumber, @Nonnull SessionDataValue<?> value) {
        DataValue<?> originValue = this.dataMap.get(dataId);
        if (originValue == null || sequenceNumber > originValue.sequenceNumber) {
            this.setValue(dataId, new DataValue<>(sequenceNumber, value));
        }
    }

    private void setValue(int dataId, DataValue<?> value) {
        this.dataMap.put(dataId, value);
        this.isDirty = true;
    }
}
