package com.qwaecd.paramagic.data.animation.track;

/**
 * 一条动画轨道的数据。
 */
public abstract class TrackData {
    public final String property;
    public final boolean isColorTrack;

    protected TrackData(String property) {
        this.property = property;
        this.isColorTrack = property.equals("color");
    }
}
