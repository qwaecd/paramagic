package com.qwaecd.paramagic.spell.view.position;

public interface PositionRule {
    /**
     * 绑定到法阵时的初始化逻辑（可缓存初始位置等）。
     */
    void onAttach(PositionRuleContext ctx);

    /**
     * 应用姿态调整。
     */
    void apply(PositionRuleContext ctx);

    /**
     * 是否需要持续更新。
     */
    boolean needsContinuousUpdate();
}
