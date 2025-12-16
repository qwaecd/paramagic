package com.qwaecd.paramagic.core.render;

import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

@SuppressWarnings("ClassCanBeRecord")
public class TransformSample {
    public final Vector3f position;
    public final Vector3f forward;
    public final Vector3f eyePosition;
    // TODO: 需要注意 up 与 y 轴平行的情况
    public final Vector3f up;

    public TransformSample(
            Vector3f position,
            Vector3f forward,
            Vector3f eyePosition,
            Vector3f up
    ) {
        this.position = position;
        this.forward = forward;
        this.eyePosition = eyePosition;
        this.up = up;
    }

    public TransformSample() {
        this.position       = new Vector3f();
        this.forward        = new Vector3f(0.0f, 0.0f, 1.0f);
        this.eyePosition    = new Vector3f();
        this.up             = new Vector3f(0.0f, 1.0f, 0.0f);
    }

    public void set(TransformSample that) {
        position.set(that.position);
        forward.set(that.forward);
        eyePosition.set(that.eyePosition);
        up.set(that.up);
    }

    public void fromEntity(Entity e) {
        this.position.set(e.position().toVector3f());
        this.forward.set(e.getLookAngle().toVector3f());
        this.eyePosition.set(e.getEyePosition().toVector3f());
        this.up.set(0.0f, 1.0f, 0.0f);
    }
}
