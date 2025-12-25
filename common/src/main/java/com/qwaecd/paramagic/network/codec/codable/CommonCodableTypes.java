package com.qwaecd.paramagic.network.codec.codable;

import com.qwaecd.paramagic.core.particle.emitter.property.type.CubeAABB;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.network.DataCodec;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
public final class CommonCodableTypes {
    public enum CommonType {
//        BYTE    (0, Byte.class),
//        SHORT   (1, Short.class),
//        INT     (2, Integer.class),
//        LONG    (3, Long.class),
//        FLOAT   (4, Float.class),
//        DOUBLE  (5, Double.class),
//        BOOLEAN (6, Boolean.class),
//        CHAR    (7, Character.class);
        VEC_2F      (20, Vector2f.class),
        VEC_3F      (21, Vector3f.class),
        VEC_4F      (22, Vector4f.class),
        QUATERNION  (23, Quaternionf.class);
        public final int id;
        public final Class<?> typeClass;
        CommonType(int id, Class<?> typeClass) {
            this.id = id;
            this.typeClass = typeClass;
        }
    }

    // Useful common types (20 ~ 39)
    public static final ICodableType<Vector2f> VEC_2F = register(CommonType.VEC_2F,
            ((codec, k) -> {
                final float[] floats = codec.readFloatArray(k);
                return new Vector2f(floats[0], floats[1]);
            }),
            (codec, k, v) -> codec.writeFloatArray(k, new float[]{v.x, v.y})
    );
    public static final ICodableType<Vector3f> VEC_3F = register(CommonType.VEC_3F,
            ((codec, k) -> {
                final float[] floats = codec.readFloatArray(k);
                return new Vector3f(floats[0], floats[1], floats[2]);
            }),
            (codec, k, v) -> codec.writeFloatArray(k, new float[]{v.x, v.y, v.z})
    );
    public static final ICodableType<Vector4f> VEC_4F = register(CommonType.VEC_4F,
            ((codec, k) -> {
                final float[] floats = codec.readFloatArray(k);
                return new Vector4f(floats[0], floats[1], floats[2], floats[3]);
            }),
            (codec, k, v) -> codec.writeFloatArray(k, new float[]{v.x, v.y, v.z, v.w})
    );
    public static final ICodableType<Quaternionf> QUATERNION = register(CommonType.QUATERNION,
            ((codec, k) -> {
                final float[] floats = codec.readFloatArray(k);
                return new Quaternionf(floats[0], floats[1], floats[2], floats[3]);
            }),
            (codec, k, v) -> codec.writeFloatArray(k, new float[]{v.x, v.y, v.z, v.w})
    );

    // Custom types below
    public static final ICodableType<CubeAABB> CUBE_AABB = registerWithClass(30, CubeAABB.class,
            (codec, k) -> codec.readObject(k, CubeAABB::fromCodec),
            (DataCodec::writeObject)
    );
    public static final ICodableType<VelocityModeStates> VELOCITY_MODE = registerWithClass(31, VelocityModeStates.class,
            ((codec, k) -> codec.readObject(k, VelocityModeStates::fromCodec)),
            (DataCodec::writeObject)
    );

    public static <T> ICodableType<T> registerWithClass(
            int typeId,
            Class<T> typeClass,
            BiFunction<DataCodec, String, T> codecTFunction,
            WriteConsumer<T> codecConsumer
    ) {
        ICodableType<T> tiCodableType = forValueWithClass(typeId, typeClass, codecTFunction, codecConsumer);
        CodableTypeRegistry.register(tiCodableType);
        return tiCodableType;
    }

    private static <T> ICodableType<T> register(
            CommonType commonType,
            BiFunction<DataCodec, String, T> codecTFunction,
            WriteConsumer<T> codecConsumer
    ) {
        ICodableType<T> tiCodableType = forValue(commonType, codecTFunction, codecConsumer);
        CodableTypeRegistry.register(tiCodableType);
        return tiCodableType;
    }

    private static <T> ICodableType<T> forValue(
            CommonType commonType,
            BiFunction<DataCodec, String, T> codecTFunction,
            WriteConsumer<T> codecConsumer
    ) {
        return new ICodableType<>() {
            @Override
            public int getTypeId() {
                return commonType.id;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Class<T> getTypeClass() {
                return (Class<T>) commonType.typeClass;
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

    private static <T> ICodableType<T> forValueWithClass(
            int typeId,
            Class<T> typeClass,
            BiFunction<DataCodec, String, T> codecTFunction,
            WriteConsumer<T> codecConsumer
    ) {
        return new ICodableType<>() {
            @Override
            public int getTypeId() {
                return typeId;
            }

            @Override
            public Class<T> getTypeClass() {
                return typeClass;
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

    static void init() {}
}
