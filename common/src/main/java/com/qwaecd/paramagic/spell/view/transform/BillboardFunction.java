package com.qwaecd.paramagic.spell.view.transform;

import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.TransformSample;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BillboardFunction {
    private final boolean lockRotation;
    private float distance;

    private final TransformSample tempSample = new TransformSample();

    private final Quaternionf tempQuat = new Quaternionf();

    public BillboardFunction(boolean lockRotation, float distance) {
        this.lockRotation = lockRotation;
        this.distance = distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return this.distance;
    }

    public void apply(Transform item, TransformSample sample) {
        Vector3f lookDir = sample.forward;
        item.setPosition(sample.eyePosition)
                .translate(
                        lookDir.x * this.distance,
                        lookDir.y * this.distance,
                        lookDir.z * this.distance
                );
        if (!lockRotation) {
            item.getRotation(this.tempQuat);
            this.tempQuat.rotationTo(0, 1, 0, lookDir.x, lookDir.y, lookDir.z);
            item.setRotation(this.tempQuat);
        }
    }

    public void apply(Transform item, CasterTransformSource source) {
        this.apply(item, source.applyTo(this.tempSample));
    }

    public boolean needsContinuousUpdate() {
        return !this.lockRotation;
    }
}
