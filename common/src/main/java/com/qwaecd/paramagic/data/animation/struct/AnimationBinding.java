package com.qwaecd.paramagic.data.animation.struct;

import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 存储单个绑定信息的类，注入动画操作发生时被消费的对象。
 */
public class AnimationBinding {
    @Getter
    @Nonnull
    private final String targetNodeNameOrComponentId;
    /**
     * The name of an external animator template name, cannot coexist with an inline animator definition.<p>
     * 从外部获取的动画模板名称，不可与内联定义同时存在。
     */
    @Getter
    @Nullable
    private final String animatorTemplateName;
    @Getter
    @Nullable
    private final AnimatorData animatorData;
    public AnimationBinding(@Nonnull String targetNodeNameOrComponentId, @Nullable String animatorTemplateName, @Nullable AnimatorData animatorData) {
        if (animatorTemplateName != null && animatorData != null) {
            throw new IllegalArgumentException("Binding cannot have both 'animatorName' and an inline 'animator' definition.");
        }
        this.targetNodeNameOrComponentId = targetNodeNameOrComponentId;
        this.animatorTemplateName = animatorTemplateName;
        this.animatorData = animatorData;
    }
}
