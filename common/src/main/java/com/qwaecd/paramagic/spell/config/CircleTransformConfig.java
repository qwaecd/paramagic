package com.qwaecd.paramagic.spell.config;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import org.joml.Vector3f;

@SuppressWarnings({"LombokGetterMayBeUsed", "ClassCanBeRecord"})
public class CircleTransformConfig implements IDataSerializable {
    private final Vector3f initialScale;
    private final Vector3f initialRotation;

    public CircleTransformConfig(Vector3f initialScale, Vector3f initialRotation) {
        this.initialScale = initialScale;
        this.initialRotation = initialRotation;
    }

    public Vector3f getInitialScale() {
        return this.initialScale;
    }

    public Vector3f getInitialRotation() {
        return this.initialRotation;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeVector3f("initialScale", this.initialScale);
        codec.writeVector3f("initialRotation", this.initialRotation);
    }

    public static CircleTransformConfig fromCodec(DataCodec codec) {
        Vector3f scale = codec.readVector3f("initialScale");
        Vector3f rotation = codec.readVector3f("initialRotation");
        return new CircleTransformConfig(scale, rotation);
    }
}
