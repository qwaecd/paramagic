package com.qwaecd.paramagic.core.particle.builder;

import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import org.joml.Vector3f;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PhysicsParamBuilder {
    private final EffectPhysicsParameter param;

    public PhysicsParamBuilder() {
        this.param = new EffectPhysicsParameter();
    }

    public PhysicsParamBuilder centerForcePos(float x, float y, float z) {
        this.param.setCFPos(x, y, z);
        return this;
    }

    public PhysicsParamBuilder centerForcePos(Vector3f v) {
        this.param.setCFPos(v.x, v.y, v.z);
        return this;
    }

    /**
     * Set center force parameters F(r) = A * pow(r, B)
     */
    public PhysicsParamBuilder centerForceParam(float A, float B) {
        this.param.setCenterForceParam(A, B);
        return this;
    }

    public PhysicsParamBuilder centerForceMaxRadius(float maxRadius) {
        this.param.setCenterForceMaxRadius(maxRadius);
        return this;
    }

    public PhysicsParamBuilder centerForceEnabled(boolean enabled) {
        this.param.setCenterForceEnabled(enabled);
        return this;
    }

    public PhysicsParamBuilder linearForce(float x, float y, float z) {
        this.param.setLinearForce(x, y, z);
        return this;
    }

    public PhysicsParamBuilder linearForce(Vector3f v) {
        this.param.setLinearForce(v.x, v.y, v.z);
        return this;
    }

    public PhysicsParamBuilder linearForceEnabled(boolean enabled) {
        this.param.setLinearForceEnabled(enabled);
        return this;
    }

    public PhysicsParamBuilder dragCoefficient(float dragCoefficient) {
        this.param.setDragCoefficient(dragCoefficient);
        return this;
    }

    public EffectPhysicsParameter build() {
        return this.param;
    }
}
