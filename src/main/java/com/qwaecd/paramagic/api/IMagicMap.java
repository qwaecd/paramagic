package com.qwaecd.paramagic.api;

public interface IMagicMap {
    /**
     * 在给定context下执行法术的效果
     * Execute the magic map with the given context
     * @param context 执行法术上下文。The mana context containing execution information
     * @return 执行结果。ExecutionResult containing success status and any return data
     */
    ExecutionResult execute(ManaContext context);

    /**
     * 获取法术枚举类型
     * Get the type of this magic
     */
    MagicType getType();

    /**
     * 获取法术需要消耗的魔力
     * Get the mana cost for this magic
     */
    int getManaCost();

    /**
     * 获取该法术映射的唯一标识符
     * Get unique identifier for this magic
     */
    String getId();

    /**
     * 获取法术名字
     * Get display name for UI
     */
    String getDisplayName();

    /**
     * 获取法术描述
     * Get description for UI
     */
    String getDescription();
}
