package com.qwaecd.paramagic.core.render;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
    @Getter
    private final Vector3f position;
    @Getter
    private final Quaternionf rotation;
    @Getter
    private final Vector3f scale;

    private final Matrix4f modelMatrix;
    private boolean isDirty;

    public Transform() {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Quaternionf().identity();
        this.scale = new Vector3f(1, 1, 1);
        this.modelMatrix = new Matrix4f().identity();
        this.isDirty = false;
    }

    public Transform setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        this.isDirty = true;
        return this;
    }

    public Transform translate(float dx, float dy, float dz) {
        this.position.add(dx, dy, dz);
        this.isDirty = true;
        return this;
    }

    public Transform setRotation(float angle, Vector3f axis) {
        this.rotation.fromAxisAngleRad(axis, angle);
        this.isDirty = true;
        return this;
    }

    public Transform rotate(float angle, Vector3f axis) {
        this.rotation.rotateAxis(angle, axis);
        this.isDirty = true;
        return this;
    }

    public Transform setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
        this.isDirty = true;
        return this;
    }

    /**
     * @return The model matrix for this transform.
     */
    public Matrix4f getModelMatrix() {
        if (isDirty) {
            modelMatrix.identity()
                    .translate(position)
                    .rotate(rotation)
                    .scale(scale);
            isDirty = false;
        }
        return modelMatrix;
    }
}
