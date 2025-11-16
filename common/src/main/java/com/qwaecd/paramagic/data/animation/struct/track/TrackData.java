package com.qwaecd.paramagic.data.animation.struct.track;

import com.qwaecd.paramagic.data.animation.property.AnimatableProperty;
import com.qwaecd.paramagic.network.IDataSerializable;

/**
 * 一条动画轨道的数据。
 */
public abstract class TrackData<T> implements IDataSerializable {
    public final AnimatableProperty<T> property;
    // 该字段不需要序列化
    public final boolean isMaterialTrack;

    protected TrackData(AnimatableProperty<T> property) {
        this.property = property;
        String name = property.getName();
        this.isMaterialTrack = name.equals("color") || name.equals("emissiveColor") || name.equals("emissiveIntensity");
    }
}
