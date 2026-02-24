package com.qwaecd.paramagic.ui.animation;

public interface ValueSetter<T> {
    /**
     * 将插值值设置到目标属性上
     * @param interpolationValue 插值后的值
     */
    void set(T interpolationValue);
}
