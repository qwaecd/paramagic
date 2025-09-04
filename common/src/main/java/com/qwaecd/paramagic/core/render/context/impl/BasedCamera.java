package com.qwaecd.paramagic.core.render.context.impl;

import com.qwaecd.paramagic.core.render.things.ICamera;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class BasedCamera implements ICamera {
    private final Vector3d position;
    private final Quaternionf rotation;

    public BasedCamera(Vector3d position, Quaternionf rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    @Override
    public Vector3d position() {
        return this.position;
    }

    @Override
    public Quaternionf rotation() {
        return this.rotation;
    }
}
