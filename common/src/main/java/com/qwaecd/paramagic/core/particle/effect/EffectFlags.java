package com.qwaecd.paramagic.core.particle.effect;

import com.qwaecd.paramagic.tools.BitmaskUtils;

@SuppressWarnings("PointlessBitwiseExpression")
public enum EffectFlags {
    // #define EFFECT_FLAG_IS_ALIVE    (1u << 0)
    // #define EFFECT_FLAG_KILL_ALL    (1u << 1)
    IS_ALIVE(1 << 0),
    KILL_ALL(1 << 1);

    private final int bit;
    EffectFlags(int bit) {
        this.bit = bit;
    }
    public int get() {
        return this.bit;
    }

    /**
     * 判断给定的位掩码（mask）是否包含当前这个标志位。<br>
     * 注意：这里的参数必须是“标志位的组合”（通常由多个 EffectFlags 的 {@code get()} 值按位或得到），
     * 不能传入其它无关数值（例如对象/效果的 ID）。
     * <p>
     * 示例：
     * <pre>
     * // 组合一个标志位掩码
     * int mask = EffectFlags.IS_ALIVE.get() | EffectFlags.KILL_ALL.get();
     * // 判断 mask 中是否包含 KILL_ALL 位
     * boolean willKillAll = EffectFlags.KILL_ALL.in(mask); // -> true
     * </pre>
     *
     * @param mask 标志位掩码（由一个或多个 EffectFlags 的位按位或 | 组合而成）。
     * @return 如果 {@code mask} 中包含当前这个标志位，返回 true；否则返回 false。
     */
    public boolean in(int mask) {
        return BitmaskUtils.hasFlag(mask, this.bit);
    }
}
