package com.qwaecd.paramagic.data.animation.struct;

import lombok.Getter;

import java.util.List;

public class AnimationBindingConfig {
    public static final String schemaVersion = "1.0";
    @Getter
    private final List<AnimationBinding> bindings;

    public AnimationBindingConfig(List<AnimationBinding> bindings) {
        this.bindings = bindings;
    }
}
