package com.qwaecd.paramagic.core.particle.emitter.property;


import com.qwaecd.paramagic.core.particle.data.EmissionRequest;

import java.util.function.Consumer;

/**
 * 一个包装了发射器属性及其更新逻辑的类。
 * @param <T> 属性值的类型 (例如 Vector3f, Float, Vector4f)
 */
public class EmitterProperty<T> {
    private T value;
    private boolean isDirty;
    private final PropertyUpdater<T> updater;
    private final Consumer<T> modifierCallback;

    /**
     * 构造一个发射器属性。
     * @param initialValue 初始值
     * @param updater 更新逻辑，定义了如何将此属性的值应用到EmissionRequest上。
     */
    public EmitterProperty(T initialValue, PropertyUpdater<T> updater) {
        this.value = initialValue;
        this.updater = updater;
        this.modifierCallback = (v) -> {};
        this.isDirty = true;
    }

    /**
     * 构造一个发射器属性。
     * @param initialValue 初始值
     * @param updater 更新逻辑，定义了如何将此属性的值应用到EmissionRequest上。
     * @param modifierCallback 每当属性被修改时调用的回调函数。
     */
    public EmitterProperty(T initialValue, PropertyUpdater<T> updater, Consumer<T> modifierCallback) {
        this.value = initialValue;
        this.updater = updater;
        this.modifierCallback = modifierCallback;
        this.isDirty = true;
    }

    public T get() {
        return this.value;
    }

    public void set(T newValue) {
        this.value = newValue;
        this.modifierCallback.accept(this.value);
        this.isDirty = true;
    }

    /**
     * @param modifierAction 一个函数，它接收内部对象并对其进行修改。
     */
    public void modify(Consumer<T> modifierAction) {
        modifierAction.accept(this.value);
        this.modifierCallback.accept(this.value);
        this.isDirty = true;
    }

    /**
     * 如果属性是脏的，则使用提供的更新器来更新EmissionRequest。
     * @param request 要更新的EmissionRequest实例。
     */
    public void updateRequestIfDirty(EmissionRequest request) {
        if (this.isDirty) {
            this.updater.accept(request, this.value);
            this.isDirty = false;
        }
    }

    @FunctionalInterface
    public interface PropertyUpdater<T> {
        void accept(EmissionRequest req, T value);
    }
}
