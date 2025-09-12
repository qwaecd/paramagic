package com.qwaecd.paramagic.client.animation;

public record Keyframe<T>(float time, T value, String interpolation) {

    public Keyframe(float time, T value) {
        this(time, value, "linear");
    }
}
