package com.qwaecd.paramagic.data.animation.struct.track;

import com.qwaecd.paramagic.data.animation.property.AnimatableProperty;

/**
 * 一条动画轨道的数据。
 */
public abstract class TrackData<T> {
    public final AnimatableProperty<T> property;
    public final boolean isColorTrack;

    protected TrackData(AnimatableProperty<T> property) {
        this.property = property;
        String name = property.getName();
        this.isColorTrack = name.equals("color") || name.equals("emissiveColor") || name.equals("emissiveIntensity");
    }
}
