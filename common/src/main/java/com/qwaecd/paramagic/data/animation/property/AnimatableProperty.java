package com.qwaecd.paramagic.data.animation.property;


import com.qwaecd.paramagic.client.animation.AccessorFactory;
import lombok.Getter;

public class AnimatableProperty<T> {
    @Getter
    private final String name;
    @Getter
    private final Class<T> valueClass;
    @Getter
    private final AccessorFactory<T> accessorFactory;

    AnimatableProperty(String name, Class<T> valueClass, AccessorFactory<T> accessorFactory) {
        this.name = name;
        this.valueClass = valueClass;
        this.accessorFactory = accessorFactory;
    }
}
