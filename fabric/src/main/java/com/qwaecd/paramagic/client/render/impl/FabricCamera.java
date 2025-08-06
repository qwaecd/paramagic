package com.qwaecd.paramagic.client.render.impl;

import com.qwaecd.paramagic.core.render.things.ICamera;
import net.minecraft.client.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class FabricCamera implements ICamera {
    private Vector3d position;
    private Quaternionf rotation;

    public FabricCamera(Camera camera) {
        this.position = new Vector3d(camera.getPosition().toVector3f());
        this.rotation = camera.rotation();
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
