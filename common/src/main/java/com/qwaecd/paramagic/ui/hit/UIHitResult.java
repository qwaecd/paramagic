package com.qwaecd.paramagic.ui.hit;

import com.qwaecd.paramagic.ui.core.UINode;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Deque;

public class UIHitResult {
    private final Deque<UINode> hitPath;

    private UIHitResult(Deque<UINode> hitPath) {
        this.hitPath = hitPath;
    }

    public void pushNode(@Nonnull UINode node) {
        this.hitPath.push(node);
    }

    /**
     * 获取命中路径，栈顶为最深节点
     */
    public Deque<UINode> getHitPath() {
        return this.hitPath;
    }

    public boolean isEmpty() {
        return this.hitPath.isEmpty();
    }


    public static UIHitResult createEmpty() {
        return new UIHitResult(new ArrayDeque<>());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UIHitResult{");
        for (UINode node : this.hitPath) {
            sb.append(node.getClass().getSimpleName()).append("->");
        }
        if (!this.hitPath.isEmpty()) {
            sb.setLength(sb.length() - 2); // 移除最后的箭头
        }
        sb.append("}");
        return sb.toString();
    }
}
