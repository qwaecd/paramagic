package com.qwaecd.paramagic.network.particle.anchor;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class AnchorSpec implements IDataSerializable {
    public static final AnchorSpec STATIC_ORIGIN = new AnchorSpec(AnchorType.STATIC_POS, new Vector3f(0, 0, 0));
    @Getter
    public final AnchorType type;

    @Nullable
    private final Vector3f position;
    private final int entityId;

    private AnchorSpec(AnchorType type, @Nonnull Vector3f position) {
        this.type = type;
        this.position = position;
        this.entityId = -1;
    }

    private AnchorSpec(AnchorType type, int entityId) {
        this.type = type;
        this.entityId = entityId;
        this.position = null;
    }

    public static AnchorSpec forStaticPosition(@Nonnull Vector3f position) {
        return new AnchorSpec(AnchorType.STATIC_POS, position);
    }

    public static AnchorSpec forBlockPosition(@Nonnull Vector3f position) {
        return new AnchorSpec(AnchorType.BLOCK, position);
    }

    public static AnchorSpec forEntity(int entityId) {
        return new AnchorSpec(AnchorType.ENTITY, entityId);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("type", this.type.id);
        switch (this.type) {
            case STATIC_POS, BLOCK -> codec.writeVector3f("position", Objects.requireNonNull(this.position));
            case ENTITY -> codec.writeInt("entityId", this.entityId);
        }
    }

    public static AnchorSpec fromCodec(DataCodec codec) {
        int typeId = codec.readInt("type");
        AnchorType type = AnchorType.fromId(typeId);
        switch (type) {
            case STATIC_POS, BLOCK -> {
                Vector3f position = codec.readVector3f("position");
                return new AnchorSpec(type, position);
            }
            case ENTITY -> {
                int entityId = codec.readInt("entityId");
                return new AnchorSpec(type, entityId);
            }
        }
        throw new IllegalArgumentException("Invalid AnchorType id: " + typeId);
    }
}
