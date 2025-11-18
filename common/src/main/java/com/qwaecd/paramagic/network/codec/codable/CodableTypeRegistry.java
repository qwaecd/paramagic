package com.qwaecd.paramagic.network.codec.codable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.qwaecd.paramagic.network.DataCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class CodableTypeRegistry {
    private static final BiMap<Class<?>, Integer> typeToIdBiMap = HashBiMap.create();
    // Single map binding id to entry holding all type info.
    private static final Map<Integer, CodableEntry<?>> idToEntry = new HashMap<>();

    // Internal entry tying everything together
    private static final class CodableEntry<T> {
        final int id;
        final Class<T> clazz;
        final BiFunction<DataCodec, String, T> deserializer;
        final WriteConsumer<T> serializer;
        CodableEntry(int id, Class<T> clazz, BiFunction<DataCodec, String, T> deserializer, WriteConsumer<T> serializer) {
            this.id = id;
            this.clazz = clazz;
            this.deserializer = deserializer;
            this.serializer = serializer;
        }
    }

    public static void init() {
        PrimitiveCodableTypes.init();
        CommonCodableTypes.init();
    }

    public static BiFunction<DataCodec, String, ?> getDeserializer(int id) {
        CodableEntry<?> entry = idToEntry.get(id);
        if (entry == null) {
            throw new NullPointerException("Can not found deserializer for id: " + id);
        }
        return entry.deserializer;
    }

    // Generic variant used at call sites to get strongly typed serializer
    @SuppressWarnings("unchecked")
    public static <T> WriteConsumer<T> getSerializer(int id) {
        CodableEntry<?> entry = idToEntry.get(id);
        if (entry == null) {
            throw new NullPointerException("Can not found serializer for id: " + id);
        }
        return (WriteConsumer<T>) entry.serializer;
    }

    // Overload providing explicit class to help at call sites with inference & validation
    @SuppressWarnings("unchecked")
    public static <T> WriteConsumer<T> getSerializer(int id, Class<T> clazz) {
        CodableEntry<?> raw = idToEntry.get(id);
        if (raw == null) {
            throw new NullPointerException("Can not found serializer for id: " + id);
        }
        if (!raw.clazz.equals(clazz)) {
            // Allow assignable (e.g., subclass) but generally expect exact match for primitives/wrappers
            if (!clazz.isAssignableFrom(raw.clazz)) {
                throw new IllegalStateException("Registered class " + raw.clazz.getName() + " does not match requested class " + clazz.getName() + " for id " + id);
            }
        }
        return (WriteConsumer<T>) raw.serializer;
    }

    public static int getTypeId(Class<?> clazz) {
        Integer i = typeToIdBiMap.get(clazz);
        if (i == null) {
            throw new NullPointerException("Can not found type ID for class: " + clazz.getName());
        }
        return i;
    }

    public static Class<?> getTypeClass(int id) {
        CodableEntry<?> entry = idToEntry.get(id);
        if (entry == null) {
            throw new NullPointerException("Can not found type class for id: " + id);
        }
        return entry.clazz;
    }

    /**
     * @param id           固定的类型 ID
     * @param clazz        类型
     * @param deserializer 反序列化逻辑
     */
    public static <T> void register(int id, Class<T> clazz, BiFunction<DataCodec, String, T> deserializer, WriteConsumer<T> serializer) {
        if (typeToIdBiMap.containsValue(id)) {
            throw new IllegalStateException("ID " + id + " is already registered for another type: " + typeToIdBiMap.inverse().get(id));
        }
        if (typeToIdBiMap.containsKey(clazz)) {
            throw new IllegalStateException("Class " + clazz.getName() + " is already registered with another ID.");
        }
        if (deserializer == null || serializer == null) {
            throw new IllegalArgumentException("Deserializer and serializer must not be null for type: " + clazz.getName());
        }
        typeToIdBiMap.put(clazz, id);
        idToEntry.put(id, new CodableEntry<>(id, clazz, deserializer, serializer));
    }

    public static <T> void register(ICodableType<T> type) {
        register(type.getTypeId(), type.getTypeClass(), type.deserializer(), type.serializer());
    }
}
