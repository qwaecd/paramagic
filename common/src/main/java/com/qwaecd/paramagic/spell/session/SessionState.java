package com.qwaecd.paramagic.spell.session;

public enum SessionState {
    RUNNING(0),             // 正常运行
    FINISHED_LOGICALLY(1),  // 逻辑已结束，但尚未清理所有资源
    INTERRUPTED(2),         // 被打断（包括松手/外力）
    DISPOSED(3);            // 已彻底清理，Manager 不再持有引用
    public final int id;
    SessionState(int id) {
        this.id = id;
    }
    public static SessionState fromId(int id) {
        for (SessionState state : values()) {
            if (state.id == id) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid SessionState id: " + id);
    }
}
