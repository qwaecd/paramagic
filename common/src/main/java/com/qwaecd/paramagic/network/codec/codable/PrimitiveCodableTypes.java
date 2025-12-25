package com.qwaecd.paramagic.network.codec.codable;

import com.qwaecd.paramagic.network.DataCodec;

import java.util.function.BiFunction;

public final class PrimitiveCodableTypes {
    public enum PrimitiveType {
        BYTE    (0, Byte.class),
        SHORT   (1, Short.class),
        INT     (2, Integer.class),
        LONG    (3, Long.class),
        FLOAT   (4, Float.class),
        DOUBLE  (5, Double.class),
        BOOLEAN (6, Boolean.class),
        CHAR    (7, Character.class);
        public final int id;
        public final Class<?> typeClass;
        PrimitiveType(int id, Class<?> typeClass) {
            this.id = id;
            this.typeClass = typeClass;
        }
    }

    public static final ICodableType<Byte>      BYTE_TYPE   =  register(PrimitiveType.BYTE,     ((codec, k) -> (byte) codec.readInt(k)),  ((codec, k, v) -> codec.writeInt(k, v)));
    public static final ICodableType<Short>     SHORT_TYPE  =  register(PrimitiveType.SHORT,    ((codec, k) -> (short) codec.readInt(k)), ((codec, k, v) -> codec.writeInt(k, v)));
    public static final ICodableType<Integer>   INT_TYPE    =  register(PrimitiveType.INT,      DataCodec::readInt, DataCodec::writeInt);
    public static final ICodableType<Long>      LONG_TYPE   =  register(PrimitiveType.LONG,     DataCodec::readLong, DataCodec::writeLong);
    public static final ICodableType<Float>     FLOAT_TYPE  =  register(PrimitiveType.FLOAT,    DataCodec::readFloat, DataCodec::writeFloat);
    public static final ICodableType<Boolean>   BOOL_TYPE   =  register(PrimitiveType.BOOLEAN,  DataCodec::readBoolean, DataCodec::writeBoolean);

    private static <T> ICodableType<T> register(
            PrimitiveType primitiveType,
            BiFunction<DataCodec, String, T> codecTFunction,
            WriteConsumer<T> codecConsumer
    ) {
        ICodableType<T> tiCodableType = forValue(primitiveType, codecTFunction, codecConsumer);
        CodableTypeRegistry.register(tiCodableType);
        return tiCodableType;
    }

    private static <T> ICodableType<T> forValue(
            PrimitiveType primitiveType,
            BiFunction<DataCodec, String, T> codecTFunction,
            WriteConsumer<T> codecConsumer
    ) {
        return new ICodableType<>() {
            @Override
            public int getTypeId() {
                return primitiveType.id;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Class<T> getTypeClass() {
                return (Class<T>) primitiveType.typeClass;
            }

            @Override
            public WriteConsumer<T> serializer() {
                return codecConsumer;
            }

            @Override
            public BiFunction<DataCodec, String, T> deserializer() {
                return codecTFunction;
            }
        };
    }

    static void init() {
    }
}
