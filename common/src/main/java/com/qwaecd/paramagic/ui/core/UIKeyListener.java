package com.qwaecd.paramagic.ui.core;

/**
 * 接收 Screen 键盘输入的 UI 行为模块。返回 true 表示事件已被消费。
 */
@FunctionalInterface
public interface UIKeyListener {
    boolean onKeyPressed(UIManager manager, int keyCode, int scanCode, int modifiers);
}
