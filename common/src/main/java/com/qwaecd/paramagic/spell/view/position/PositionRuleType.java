package com.qwaecd.paramagic.spell.view.position;

public enum PositionRuleType {
    FOLLOW_CASTER_FEET(0),      // 跟随施法者脚底
    FOLLOW_CASTER_EYE(1),       // 跟随施法者眼睛位置
    FIXED_AT_CASTER_FEET(2),    // 固定在施法者脚底（施法开始时的位置）
    IN_FRONT_OF_CASTER(3),      // 在施法者面前（视线方向）
    CUSTOM(99);                 // 自定义规则

    public final int id;

    PositionRuleType(int id) {
        this.id = id;
    }

    public static PositionRuleType fromId(int id) {
        for (PositionRuleType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PositionRuleType id: " + id);
    }
}
