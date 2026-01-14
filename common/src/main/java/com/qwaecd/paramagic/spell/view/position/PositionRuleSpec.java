package com.qwaecd.paramagic.spell.view.position;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;
import org.joml.Vector3f;

/**
 * 纯数据的规则描述，用于序列化与构建运行时策略。
 */
@SuppressWarnings({"LombokGetterMayBeUsed", "ClassCanBeRecord"})
public class PositionRuleSpec implements IDataSerializable {
    @Getter
    private final PositionRuleType type;
    private final Vector3f offset;
    private final boolean lockRotation;
    private final Vector3f rotationOffset;

    public PositionRuleSpec(
            PositionRuleType type,
            Vector3f offset,
            boolean lockRotation,
            Vector3f rotationOffset
    ) {
        this.type = type;
        this.offset = new Vector3f(offset);
        this.lockRotation = lockRotation;
        this.rotationOffset = new Vector3f(rotationOffset);
    }

    public Vector3f getOffset() {
        return new Vector3f(offset);
    }

    public boolean isLockRotation() {
        return this.lockRotation;
    }

    public Vector3f getRotationOffset() {
        return new Vector3f(rotationOffset);
    }

    public static PositionRuleSpec fixedAtCasterFeet() {
        return new PositionRuleSpec(
                PositionRuleType.FIXED_AT_CASTER_FEET,
                new Vector3f(0.0f, 0.01f, 0.0f),
                true,
                new Vector3f()
        );
    }

    public static PositionRuleSpec of(PositionRuleType type, Vector3f offset, boolean lockRotation, Vector3f rotationOffset) {
        return new PositionRuleSpec(type, offset, lockRotation, rotationOffset);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("type", this.type.id);
        codec.writeVector3f("offset", this.offset);
        codec.writeBoolean("lockRotation", this.lockRotation);
        codec.writeVector3f("rotationOffset", this.rotationOffset);
    }

    public static PositionRuleSpec fromCodec(DataCodec codec) {
        PositionRuleType type = PositionRuleType.fromId(codec.readInt("type"));
        Vector3f offset = codec.readVector3f("offset");
        boolean lockRotation = codec.readBoolean("lockRotation");
        Vector3f rotationOffset = codec.readVector3f("rotationOffset");
        return new PositionRuleSpec(type, offset, lockRotation, rotationOffset);
    }
}
