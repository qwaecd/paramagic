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
     * Set center force parameters {@code F(r) = A * pow(r, B) ...;}
     */
    public PhysicsParamBuilder primaryForceParam(float A, float B) {
        this.param.setPrimaryForceParam(A, B);
        return this;
    }

    public PhysicsParamBuilder primaryForceMaxRadius(float maxRadius) {
        this.param.setPrimaryForceMaxRadius(maxRadius);
        return this;
    }

    public PhysicsParamBuilder primaryForceEnabled(boolean enabled) {
        this.param.setPrimaryForceEnabled(enabled);
        return this;
    }


    /**
     * Set center force parameters {@code F(r) = ... C * pow(r, D) ...;}
     */
    public PhysicsParamBuilder secondaryForceParam(float C, float D) {
        this.param.setSecondaryForceParam(C, D);
        return this;
    }

    public PhysicsParamBuilder secondaryForceMaxRadius(float maxRadius) {
        this.param.setSecondaryForceMaxRadius(maxRadius);
        return this;
    }

    public PhysicsParamBuilder secondaryForceEnabled(boolean enabled) {
        this.param.setSecondaryForceEnabled(enabled);
        return this;
    }

    /**
     * Set sinusoidal force parameters {@code F(r) = ... E * sin(F * r + phase) * pow(r, G);}
     */
    public PhysicsParamBuilder sinusoidalForceParam(float E, float F, float G) {
        this.param.setSinusoidalForceParam(E, F, G);
        return this;
    }

    public PhysicsParamBuilder sinusoidalForceMaxRadius(float maxRadius) {
        this.param.setSinusoidalForceMaxRadius(maxRadius);
        return this;
    }

    public PhysicsParamBuilder sinusoidalExtraParam(float phase) {
        this.param.setSinusoidalExtraParam(phase);
        return this;
    }

    public PhysicsParamBuilder sinusoidalForceEnabled(boolean enabled) {
        this.param.setSinusoidalForceEnabled(enabled);
        return this;
    }


    // linear force
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
