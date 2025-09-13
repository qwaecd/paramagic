package com.qwaecd.paramagic.data.animation;

import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BindingData {
    @Getter
    @Nonnull
    private final String targetComponentId;
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
    public BindingData(@Nonnull String targetComponentId, @Nullable String animatorTemplateName, @Nullable AnimatorData animatorData) {
        if (animatorTemplateName != null && animatorData != null) {
            throw new IllegalArgumentException("Binding cannot have both 'animatorName' and an inline 'animator' definition.");
        }
        this.targetComponentId = targetComponentId;
        this.animatorTemplateName = animatorTemplateName;
        this.animatorData = animatorData;
    }
}
