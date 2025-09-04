package com.qwaecd.paramagic.client.render.impl;

import com.qwaecd.paramagic.core.render.things.ICamera;
import net.minecraft.client.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class FabricCamera implements ICamera {
    private Camera camera;
    private Vector3d position;

    public FabricCamera(Camera camera) {
        this.position = new Vector3d(camera.getPosition().toVector3f());
        this.camera = camera;
    }

    @Override
    public Vector3d position() {
        return this.position;
    }

    @Override
    public Quaternionf rotation() {
        return this.camera.rotation();
    }
}
