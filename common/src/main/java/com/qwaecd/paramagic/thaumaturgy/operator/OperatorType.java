package com.qwaecd.paramagic.thaumaturgy.operator;

public enum OperatorType {
    PROJECTILE  (100, 0),
    MODIFIER    (200, 1),
    ALPHA       (300, 2),
    FLOW        (400, 3);

    public final int priority;
    public final int id;
    OperatorType(int priority, int id) {
        this.priority = priority;
        this.id = id;
    }

    public int getPriority() {
        return this.priority;
    }

    public static OperatorType fromId(int id) {
        for (OperatorType value : OperatorType.values()) {
            if (value.id == id) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid OperatorType id: " + id);
    }
}
