package com.qwaecd.paramagic.tools.anim;

/**
 * 插值函数，决定了给定动画进度 alpha 的情况下如何获取 start 和 end 之间的值。
 * @param <T> 被插值的类型。
 */
public interface Interpolator<T> {
    /**
     * 依据 alpha 值在 start 和 end 之间进行插值计算
     * @param start 起始值
     * @param end 结束值
     * @param alpha 插值因子，范围通常在 0 到 1 之间，表示从 start 到 end 的进度
     * @return 插值计算后的结果
     */
    T interpolate(T start, T end, float alpha);
}
