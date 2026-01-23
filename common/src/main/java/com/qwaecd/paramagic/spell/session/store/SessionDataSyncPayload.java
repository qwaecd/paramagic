package com.qwaecd.paramagic.spell.session.store;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("ClassCanBeRecord")
public class SessionDataSyncPayload implements IDataSerializable {
    public final UUID sessionId;

    @Nonnull
    private final List<Entry> entries;

    public SessionDataSyncPayload(UUID sessionId, int dataId, int sequenceNumber, @Nonnull SessionDataValue<?> value) {
        this(sessionId, Collections.singletonList(new Entry(dataId, sequenceNumber, value)));
    }

    public SessionDataSyncPayload(UUID sessionId, @Nonnull List<Entry> entries) {
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("SessionDataSyncPayload entries cannot be empty");
        }
        this.sessionId = sessionId;
        this.entries = List.copyOf(entries);
    }

    @Nonnull
    public List<Entry> getEntries() {
        return this.entries;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeString("sessionId", this.sessionId.toString());
        codec.writeInt("entryCount", this.entries.size());
        for (Entry entry : this.entries) {
            entry.write(codec);
        }
    }

    public static SessionDataSyncPayload fromCodec(DataCodec codec) {
        final UUID sessionId = UUID.fromString(codec.readString("sessionId"));
        final int entryCount = codec.readInt("entryCount");
        List<Entry> entries = new ArrayList<>(entryCount);
        for (int i = 0; i < entryCount; i++) {
            entries.add(Entry.fromCodec(codec));
        }
        return new SessionDataSyncPayload(sessionId, entries);
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class Entry implements IDataSerializable {
        public final int dataId;
        public final int sequenceNumber;
        @Nonnull
        public final SessionDataValue<?> value;

        public Entry(int dataId, int sequenceNumber, @Nonnull SessionDataValue<?> value) {
            this.dataId = dataId;
            this.sequenceNumber = sequenceNumber;
            this.value = value;
        }

        @Override
        public void write(DataCodec codec) {
            codec.writeInt("dataId", this.dataId);
            codec.writeInt("sequenceNumber", this.sequenceNumber);
            codec.writeObject("value", this.value);
        }

        public static Entry fromCodec(DataCodec codec) {
            final int dataId = codec.readInt("dataId");
            final int sequenceNumber = codec.readInt("sequenceNumber");
            final SessionDataValue<?> value = codec.readObject("value", SessionDataValue::fromCodec);
            return new Entry(dataId, sequenceNumber, value);
        }
    }
}
