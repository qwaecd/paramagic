package com.qwaecd.paramagic.spell.view.position;

import com.qwaecd.paramagic.core.render.TransformSample;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CirclePositionRule implements IDataSerializable {
    public static final CirclePositionRule fixedAtCasterFeet = new CirclePositionRule(
            PositionRuleType.FIXED_AT_CASTER_FEET,
            new Vector3f(0.0f, 0.01f, 0.0f),
            true,
            new Vector3f()
    );
    private final PositionRuleType type;

    private final Vector3f offset;

    private final boolean lockRotation;

    private final Vector3f rotationOffset;

    public CirclePositionRule(
            PositionRuleType type,
            Vector3f offset,
            boolean lockRotation,
            Vector3f rotationOffset
    ) {
        this.type = type;
        this.offset = offset;
        this.lockRotation = lockRotation;
        this.rotationOffset = rotationOffset;
    }

    @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
    public void applyPosition(MagicCircle circle, TransformSample casterTransform) {
        switch (type) {
            case FOLLOW_CASTER_FEET, FIXED_AT_CASTER_FEET -> {
                circle.getTransform().setPosition(
                        casterTransform.position.x + offset.x,
                        casterTransform.position.y + offset.y,
                        casterTransform.position.z + offset.z
                );
            }
            case FOLLOW_CASTER_EYE -> {
                circle.getTransform().setPosition(
                        casterTransform.eyePosition.x + offset.x,
                        casterTransform.eyePosition.y + offset.y,
                        casterTransform.eyePosition.z + offset.z
                );
            }
            case IN_FRONT_OF_CASTER -> {
                Vector3f lookDir = casterTransform.forward;
                float distance = offset.length();
                circle.getTransform()
                        .setPosition(casterTransform.eyePosition)
                        .translate(
                                lookDir.x * distance,
                                lookDir.y * distance,
                                lookDir.z * distance
                        );
                if (!lockRotation) {
                    float yaw = (float) Math.atan2(lookDir.z, lookDir.x);
                    circle.getTransform().setRotation(
                            new Quaternionf().rotateY(-yaw).rotateYXZ(
                                    rotationOffset.y,
                                    rotationOffset.x,
                                    rotationOffset.z
                            )
                    );
                }
            }
        }
    }

    public boolean shouldUpdatePerTick() {
        return type == PositionRuleType.FOLLOW_CASTER_FEET
                || type == PositionRuleType.FOLLOW_CASTER_EYE
                || (type == PositionRuleType.IN_FRONT_OF_CASTER && !lockRotation);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("type", this.type.id);
        codec.writeVector3f("offset", this.offset);
        codec.writeBoolean("lockRotation", lockRotation);
        codec.writeVector3f("rotationOffset", rotationOffset);
    }

    public static CirclePositionRule fromCodec(DataCodec codec) {
        int typeId = codec.readInt("type");
        PositionRuleType type = PositionRuleType.fromId(typeId);
        Vector3f offset = codec.readVector3f("offset");
        boolean lockRotation = codec.readBoolean("lockRotation");
        Vector3f rotationOffset = codec.readVector3f("rotationOffset");
        return new CirclePositionRule(type, offset, lockRotation, rotationOffset);
    }
}
