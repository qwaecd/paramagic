package com.qwaecd.paramagic.spell.core;

public enum SessionState {
    RUNNING,            // 正常运行
    STOPPING,           // 已发出停止指令，正在等待资源清理
    DISPOSED            // 已彻底清理资源
}
