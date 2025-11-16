package com.qwaecd.paramagic.data.animation.struct.track;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.qwaecd.paramagic.network.DataCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registry for TrackData concrete types used by AnimatorData serialization.<br>
 * Each registered track type has a unique integer ID and a factory to read it from a DataCodec.
 */
public final class TrackTypeRegistry {
    private static final BiMap<Class<? extends TrackData<?>>, Integer> classToId = HashBiMap.create();
    private static final Map<Integer, Function<DataCodec, ? extends TrackData<?>>> idToFactory = new HashMap<>();

    private TrackTypeRegistry() {}

    /**
     * Register a track data type.
     * @param id unique type id (stable within your protocol)
     * @param trackClass the concrete TrackData class
     * @param factory a factory that reads an instance from the codec (must match write order in that class)
     */
    public static synchronized <T extends TrackData<?>> void register(int id, Class<T> trackClass, Function<DataCodec, T> factory) {
        if (classToId.containsValue(id)) {
            Class<? extends TrackData<?>> existing = classToId.inverse().get(id);
            throw new IllegalStateException("Track type id " + id + " already registered for class: " + existing);
        }
        if (classToId.containsKey(trackClass)) {
            throw new IllegalStateException("Track class already registered: " + trackClass.getName());
        }
        if (factory == null) {
            throw new IllegalArgumentException("Factory must not be null for class: " + trackClass.getName());
        }
        //noinspection RedundantCast
        classToId.put((Class<? extends TrackData<?>>) trackClass, id);
        idToFactory.put(id, factory);
    }

    public static int getTypeId(Class<? extends TrackData<?>> clazz) {
        Integer id = classToId.get(clazz);
        if (id == null) {
            throw new NullPointerException("No track type id registered for class: " + clazz.getName());
        }
        return id;
    }

    public static Function<DataCodec, ? extends TrackData<?>> getFactory(int id) {
        Function<DataCodec, ? extends TrackData<?>> factory = idToFactory.get(id);
        if (factory == null) {
            throw new NullPointerException("No track factory registered for id: " + id);
        }
        return factory;
    }

    /*
     * Built-in registrations for core track types.
     */
    static {
        // Reserve id=1 for KeyframeTrackData
        //noinspection RedundantCast
        register(1, KeyframeTrackData.class, codec -> (KeyframeTrackData<?>) KeyframeTrackData.fromCodec(codec));
    }
}

