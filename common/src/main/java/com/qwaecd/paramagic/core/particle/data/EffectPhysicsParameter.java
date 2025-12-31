package com.qwaecd.paramagic.core.particle.data;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

/**
 * <pre>
 * struct EffectPhysicsParams {
 *     // F(r) = A * pow(r, B) + C * pow(r, D) + E * sin(F * r + phase) * pow(r, G);
 *     vec4 primaryForce; // x: A, y: B, z: maxRadius_for_primary, w: enablePrimary(0/1)
 *     vec4 secondaryForce; // x: C, y: D, z: maxRadius_for_secondary, w: enableSecondary(0/1)
 *
 *     vec4 sinusoidalForce; // sinusoidalForce: x: E, y: F, z: G, w: enableSinus(0/1)
 *     vec4 sinusoidalExtra; // sinusoidalExtra: x: phase, y,z,w = reserved
 *
 *     vec4 centerForcePos; // x, y, z: 力场中心位置, w: dragCoefficient (阻力系数), acceleration -= velocity * dragCoefficient;
 *
 *     vec4 linearForce; // x, y, z: 线性力 (e.g. gravity + wind), w: enable (0 or 1)
 * };
 * </pre>
 */
@Getter
@Setter
@PlatformScope(PlatformScopeType.COMMON)
public final class EffectPhysicsParameter implements IDataSerializable {
    private final Vector4f primaryForce;
    private final Vector4f secondaryForce;

    private final Vector4f sinusoidalForce;
    private final Vector4f sinusoidalExtra;

    private final Vector4f centerForcePos;
    private final Vector4f linearForce;

    private boolean modified = true;


    /**
     * <pre>
     * struct EffectPhysicsParams {
     *     // F(r) = A * pow(r, B) + C * pow(r, D) + E * sin(F * r + phase) * pow(r, G);
     *     vec4 primaryForce; // x: A, y: B, z: maxRadius_for_primary, w: enablePrimary(0/1)
     *     vec4 secondaryForce; // x: C, y: D, z: maxRadius_for_secondary, w: enableSecondary(0/1)
     *
     *     vec4 sinusoidalForce; // sinusoidalForce: x: E, y: F, z: G, w: enableSinus(0/1)
     *     vec4 sinusoidalExtra; // sinusoidalExtra: x: phase, z: maxRadius_for_sinusoidal, y,w = reserved
     *
     *     vec4 centerForcePos; // x, y, z: 力场中心位置, w: dragCoefficient (阻力系数), acceleration -= velocity * dragCoefficient;
     *
     *     vec4 linearForce; // x, y, z: 线性力 (e.g. gravity + wind), w: enable (0 or 1)
     * };
     * </pre>
     */
    public EffectPhysicsParameter() {
        this.primaryForce       = new Vector4f(0.0f, 0.0f, Float.MAX_VALUE, 0.0f);
        this.secondaryForce     = new Vector4f(0.0f, 0.0f, Float.MAX_VALUE, 0.0f);

        this.sinusoidalForce    = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
        this.sinusoidalExtra    = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);

        this.centerForcePos     = new Vector4f(0.0f, 0.0f, 0.0f, 0.01f);
        this.linearForce        = new Vector4f(0.0f, -0.0981f, 0.0f, 0.0f);
    }

    public EffectPhysicsParameter(
            Vector4f primaryForce,
            Vector4f secondaryForce,
            Vector4f sinusoidalForce,
            Vector4f sinusoidalExtra,
            Vector4f centerForcePos,
            Vector4f linearForce,
            boolean modified
    ) {
        this.primaryForce = primaryForce;
        this.secondaryForce = secondaryForce;
        this.sinusoidalForce = sinusoidalForce;
        this.sinusoidalExtra = sinusoidalExtra;
        this.centerForcePos = centerForcePos;
        this.linearForce = linearForce;
        this.modified = modified;
    }

    public void applyFrom(EffectPhysicsParameter other) {
        this.primaryForce   .set(other.primaryForce);
        this.secondaryForce .set(other.secondaryForce);
        this.sinusoidalForce.set(other.sinusoidalForce);
        this.sinusoidalExtra.set(other.sinusoidalExtra);
        this.centerForcePos .set(other.centerForcePos);
        this.linearForce    .set(other.linearForce);
        this.modified = true;
    }

    public void setCFPos(float x, float y, float z) {
        this.centerForcePos.x = x;
        this.centerForcePos.y = y;
        this.centerForcePos.z = z;
        this.modified = true;
    }

    public void setCFPos(Vector3f v) {
        this.setCFPos(v.x, v.y, v.z);
    }

    /**
     * {@code F(r) = A * pow(r, B) ... ;}<br>
     * A > 0 为引力，A < 0 为斥力<br>
     * A > 0 is attractive force, A < 0 is repulsive force
     */
    public void setPrimaryForceParam(float A, float B) {
        this.primaryForce.x = A;
        this.primaryForce.y = B;
        this.modified = true;
    }

    /**
     * {@code F(r) = ... C * pow(r, D) ... ;}<br>
     * C > 0 为引力，C < 0 为斥力<br>
     * C > 0 is attractive force, C < 0 is repulsive force
     */
    public void setSecondaryForceParam(float C, float D) {
        this.secondaryForce.x = C;
        this.secondaryForce.y = D;
        this.modified = true;
    }

    /**
     * {@code F(r) = ... E * sin(F * r + phase) * pow(r, G);}
     */
    public void setSinusoidalForceParam(float E, float F, float G) {
        this.sinusoidalForce.x = E;
        this.sinusoidalForce.y = F;
        this.sinusoidalForce.z = G;
        this.modified = true;
    }

    /**
     * {@code F(r) = ... E * sin(F * r + phase) * pow(r, G);}
     */
    public void setSinusoidalExtraParam(float phase) {
        this.sinusoidalExtra.x = phase;
        this.modified = true;
    }

    public void setPrimaryForceMaxRadius(float maxRadius) {
        this.primaryForce.z = maxRadius;
        this.modified = true;
    }

    public void setSecondaryForceMaxRadius(float maxRadius) {
        this.secondaryForce.z = maxRadius;
        this.modified = true;
    }

    public void setSinusoidalForceMaxRadius(float maxRadius) {
        this.sinusoidalExtra.z = maxRadius;
        this.modified = true;
    }

    public void setPrimaryForceEnabled(boolean enabled) {
        this.primaryForce.w = enabled ? 1.0f : 0.0f;
        this.modified = true;
    }

    public void setSecondaryForceEnabled(boolean enabled) {
        this.secondaryForce.w = enabled ? 1.0f : 0.0f;
        this.modified = true;
    }

    public void setSinusoidalForceEnabled(boolean enabled) {
        this.sinusoidalForce.w = enabled ? 1.0f : 0.0f;
        this.modified = true;
    }

    public void setLinearForce(float x, float y, float z) {
        this.linearForce.x = x;
        this.linearForce.y = y;
        this.linearForce.z = z;
        this.modified = true;
    }

    public void setLinearForce(Vector3f v) {
        this.setLinearForce(v.x, v.y, v.z);
    }

    public void setLinearForceEnabled(boolean enabled) {
        this.linearForce.w = enabled ? 1.0f : 0.0f;
        this.modified = true;
    }

    public void setDragCoefficient(float dragCoefficient) {
        this.centerForcePos.w = dragCoefficient;
        this.modified = true;
    }

    public boolean isDirty() {
        return this.modified;
    }

    public void setDirty() {
        this.modified = true;
    }

    public void setDirty(boolean b) {
        this.modified = b;
    }

    public void writePhysicsParamsToBuffer(ByteBuffer buffer) {
        write(this.primaryForce, buffer);
        write(this.secondaryForce, buffer);
        write(this.sinusoidalForce, buffer);
        write(this.sinusoidalExtra, buffer);
        write(this.centerForcePos, buffer);
        write(this.linearForce, buffer);
    }

    private void write(Vector4f v, ByteBuffer buffer) {
        buffer.putFloat(v.x).putFloat(v.y).putFloat(v.z).putFloat(v.w);
    }

    private static final int structMembers = 6;
    public static final int SIZE_IN_BYTES = structMembers * Float.BYTES * 4;

    @Override
    public void write(DataCodec codec) {
        codec.writeVector4f("primaryForce", this.primaryForce);
        codec.writeVector4f("secondaryForce", this.secondaryForce);
        codec.writeVector4f("sinusoidalForce", this.sinusoidalForce);
        codec.writeVector4f("sinusoidalExtra", this.sinusoidalExtra);
        codec.writeVector4f("centerForcePos", this.centerForcePos);
        codec.writeVector4f("linearForce", this.linearForce);
    }

    public static EffectPhysicsParameter fromCodec(DataCodec codec) {
        Vector4f primaryForce = codec.readVector4f("primaryForce");
        Vector4f secondaryForce = codec.readVector4f("secondaryForce");
        Vector4f sinusoidalForce = codec.readVector4f("sinusoidalForce");
        Vector4f sinusoidalExtra = codec.readVector4f("sinusoidalExtra");
        Vector4f centerForcePos = codec.readVector4f("centerForcePos");
        Vector4f linearForce = codec.readVector4f("linearForce");
        return new EffectPhysicsParameter(
                primaryForce,
                secondaryForce,
                sinusoidalForce,
                sinusoidalExtra,
                centerForcePos,
                linearForce,
                true
        );
    }
}
