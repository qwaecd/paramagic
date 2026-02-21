package com.qwaecd.paramagic.ui.core;

import java.util.function.Consumer;

public class UITask {
    public final Consumer<UIManager> task;
    public final TaskStage taskStage;

    public UITask(Consumer<UIManager> task) {
        this.task = task;
        this.taskStage = TaskStage.AFTER_EVENT;
    }

    public UITask(Consumer<UIManager> task, TaskStage stage) {
        this.task = task;
        this.taskStage = stage;
    }

    public void execute(UIManager manager) {
        this.task.accept(manager);
    }

    public static UITask create(Consumer<UIManager> task, TaskStage stage) {
        return new UITask(task, stage);
    }
}
