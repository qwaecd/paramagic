package com.qwaecd.paramagic.client.animation;

import com.qwaecd.paramagic.core.para.material.ParaMaterial;
import com.qwaecd.paramagic.core.render.Transform;

@FunctionalInterface
public interface AccessorFactory<T> {
    PropertyAccessor<T> getAccessor(Transform transform, ParaMaterial material);
}
