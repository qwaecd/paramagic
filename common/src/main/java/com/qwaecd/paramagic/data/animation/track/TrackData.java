package com.qwaecd.paramagic.data.animation.track;

/**
 * 一条动画轨道的数据。
 */
public abstract class TrackData {
    public final String property;

    protected TrackData(String property) {
        this.property = property;
    }
}
