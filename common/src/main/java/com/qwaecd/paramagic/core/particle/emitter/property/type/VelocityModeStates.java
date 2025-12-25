package com.qwaecd.paramagic.core.particle.emitter.property.type;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;

@SuppressWarnings("PointlessBitwiseExpression")
public enum VelocityModeStates implements IDataSerializable {
    /**
     * bit 001<br>
     * 锥形发射，从给定 baseVelocity 方向为轴的锥体内发射，偏离轴线的最大角度由 velocitySpread 控制。
     * PointEmitter, SphereEmitter 支持该模式。
     */
    CONE(1 << 0),   // 001
    /**
     * bit 010<br>
     * 随机发射，在初始点均匀随机发射，速度方向任意，baseVelocity 仅大小有效。
     */
    RANDOM(1 << 1), // 010
    /**
     * bit 011<br>
     * 径向发射，从发射器位置向外径向发射，速度方向为发射器位置指向粒子出生位置的方向，baseVelocity 仅大小有效。
     */
    RADIAL_FROM_CENTER(1 << 1 | 1 << 0), // 011
    /**
     * bit 100<br>
     * 直接发射，粒子速度方向与 baseVelocity 完全一致，velocitySpread 无效。
     */
    DIRECT(1 << 2); // 100

    public final int bit;
    VelocityModeStates(int bit) {
        this.bit = bit;
    }

    public static VelocityModeStates fromBit(int bit) {
        for (VelocityModeStates state : values()) {
            if (state.bit == bit) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown VelocityModeStates bit: " + bit);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("velocityMode", this.bit);
    }

    public static VelocityModeStates fromCodec(DataCodec codec) {
        return fromBit(codec.readInt("velocityMode"));
    }
}
