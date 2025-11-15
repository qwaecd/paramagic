package com.qwaecd.paramagic.spell.state.phase.property;

public enum SpellPhaseType {
    /**
     * 空闲
     */
    IDLE(0),
    /**
     * 吟唱, 蓄力
     */
    CASTING(1),
    /**
     * 持续触发
     */
    CHANNELING(2),
    /**
     * 冷却
     */
    COOLDOWN(3);
    private final int ID;
    SpellPhaseType(int id) {
        this.ID = id;
    }
    public int ID() {
        return this.ID;
    }

    public static SpellPhaseType fromID(int id) {
        for (SpellPhaseType type : SpellPhaseType.values()) {
            if (type.ID() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown SpellPhaseType ID: " + id);
    }
}
