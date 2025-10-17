package com.qwaecd.paramagic.data.animation.struct.track;

public record KeyframeData<T>(float time, T value, String interpolation) {

    public KeyframeData(float time, T value) {
        this(time, value, "linear");
    }
}
