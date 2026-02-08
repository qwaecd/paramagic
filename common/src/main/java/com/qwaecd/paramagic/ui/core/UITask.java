package com.qwaecd.paramagic.ui.core;

import java.util.function.Consumer;

public class UITask {
    public final Consumer<UIManager> task;

    public UITask(Consumer<UIManager> task) {
        this.task = task;
    }

    public void execute(UIManager manager) {
        this.task.accept(manager);
    }

    public static UITask create(Consumer<UIManager> task) {
        return new UITask(task);
    }
}
