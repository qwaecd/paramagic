package com.qwaecd.paramagic.data.animation;

import lombok.Getter;

import java.util.List;

public class AnimationBindingData {
    public static final String schemaVersion = "1.0";
    @Getter
    private final List<BindingData> bindingData;

    public AnimationBindingData(List<BindingData> bindingData) {
        this.bindingData = bindingData;
    }
}
