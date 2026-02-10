package com.qwaecd.paramagic.thaumaturgy.operator;

public enum OperatorType {
    PROJECTILE(100),
    MODIFIER(200),
    ALPHA(300),
    FLOW(400);

    public final int priority;
    OperatorType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }
}
