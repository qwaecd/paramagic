package com.qwaecd.paramagic.client.obj.laser;

import com.qwaecd.paramagic.client.material.LaserMaterial;
import com.qwaecd.paramagic.client.renderbase.RenderableObject;
import com.qwaecd.paramagic.client.renderbase.SharedMeshes;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LaserBeam extends RenderableObject {
    private static final Vector3f LOCAL_AXIS = new Vector3f(0.0f, 1.0f, 0.0f);

    public LaserBeam(LaserMaterial material) {
        super(SharedMeshes.laserCylinder(), material);
    }

    public static LaserBeam createDefault() {
        return new LaserBeam(new LaserMaterial());
    }

    public LaserBeam setBeam(Vector3f start, Vector3f end, float radius) {
        Vector3f direction = new Vector3f(end).sub(start);
        float length = direction.length();
        if (length <= 0.0001f) {
            this.transform.setPosition(start).setScale(radius, 0.0f, radius);
            return this;
        }

        direction.div(length);
        Quaternionf rotation = new Quaternionf().rotationTo(LOCAL_AXIS, direction);
        this.transform.setPosition(start).setRotation(rotation).setScale(radius, length, radius);
        return this;
    }
}
