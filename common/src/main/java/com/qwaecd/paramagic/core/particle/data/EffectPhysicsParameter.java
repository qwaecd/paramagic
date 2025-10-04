package com.qwaecd.paramagic.core.particle.data;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

/**
 * <pre>
 * struct EffectPhysicsParams {
 *     // F(r) = A * pow(r, B)
 *     vec4 centerForceParams; // x: A, y: B, z: maxRadius, w: enable (0 or 1)
 *     vec4 centerForcePos; // x, y, z: 力场中心位置, w: dragCoefficient (阻力系数), acceleration -= velocity * dragCoefficient;
 *     vec4 linearForce; // x, y, z: 线性力 (e.g. gravity + wind), w: enable (0 or 1)
 * };</pre>
 */
@Getter
@Setter
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public final class EffectPhysicsParameter {
    private Vector4f centerForceData;
    private Vector4f centerForcePos;
    private Vector4f linearForce;

    private boolean modified = true;

    public EffectPhysicsParameter() {
        this.centerForceData    = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
        this.centerForcePos     = new Vector4f(0.0f, 0.0f, 0.0f, 0.01f);
        this.linearForce        = new Vector4f(0.0f, -0.0981f, 0.0f, 0.0f);
    }

    /**
     * @param centerForceData x: A, y: B, z: maxRadius, w: enable (0 or 1)
     * @param centerForcePos x, y, z: 力场中心位置, w: dragCoefficient (阻力系数)
     * @param linearForce x, y, z: 线性力 (e.g. gravity + wind), w: enable (0 or 1)
     */
    public EffectPhysicsParameter(
            Vector4f centerForceData,
            Vector4f centerForcePos,
            Vector4f linearForce
    ) {
        this.centerForceData = centerForceData;
        this.centerForcePos = centerForcePos;
        this.linearForce = linearForce;
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

    public void setLinearForce(float x, float y, float z) {
        this.linearForce.x = x;
        this.linearForce.y = y;
        this.linearForce.z = z;
        this.modified = true;
    }

    public void seLinearForce(Vector3f v) {
        this.setLinearForce(v.x, v.y, v.z);
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
        buffer.putFloat(this.centerForceData.x);
        buffer.putFloat(this.centerForceData.y);
        buffer.putFloat(this.centerForceData.z);
        buffer.putFloat(this.centerForceData.w);

        buffer.putFloat(this.centerForcePos.x);
        buffer.putFloat(this.centerForcePos.y);
        buffer.putFloat(this.centerForcePos.z);
        buffer.putFloat(this.centerForcePos.w);

        buffer.putFloat(this.linearForce.x);
        buffer.putFloat(this.linearForce.y);
        buffer.putFloat(this.linearForce.z);
        buffer.putFloat(this.linearForce.w);
    }

    public static final int SIZE_IN_BYTES = 3 * Float.BYTES * 4;
}
