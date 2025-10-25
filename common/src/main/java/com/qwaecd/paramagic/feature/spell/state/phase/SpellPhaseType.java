package com.qwaecd.paramagic.feature.spell.state.phase;

public enum SpellPhaseType {
    /**
     * 空闲
     */
    IDLE,
    /**
     * 吟唱, 蓄力
     */
    CASTING,
    /**
     * 持续触发
     */
    CHANNELING,
    /**
     * 冷却
     */
    COOLDOWN;
}
