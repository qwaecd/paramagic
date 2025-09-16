package com.qwaecd.paramagic.data.animation.track;

import com.qwaecd.paramagic.data.animation.PropertyType;

/**
 * 一条动画轨道的数据。
 */
public abstract class TrackData<T> {
    public final PropertyType<T> property;
    public final boolean isColorTrack;

    protected TrackData(PropertyType<T> property) {
        this.property = property;
        String name = property.getName();
        this.isColorTrack = name.equals("color") || name.equals("emissiveColor") || name.equals("emissiveIntensity");
    }
}
