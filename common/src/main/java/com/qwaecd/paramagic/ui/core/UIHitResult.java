package com.qwaecd.paramagic.ui.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIHitResult {
    private final List<UINode> hitPath;

    private UIHitResult(List<UINode> hitPath) {
        this.hitPath = hitPath;
    }

    public void pushNode(@Nonnull UINode node) {
        this.hitPath.add(node);
    }

    public UINode popNode() {
        return this.hitPath.remove(this.hitPath.size() - 1);
    }

    /**
     * 获取命中路径栈顶节点, 也是最深节点, 不会移除节点
     * @return 栈顶节点, 为 null 时表示不存在
     */
    @Nullable
    public UINode getTop() {
        return this.hitPath.isEmpty() ? null : this.hitPath.get(this.hitPath.size() - 1);
    }

    /**
     * 获取命中路径，索引越大越深（最后一个是目标节点，第一个是根）<br>
     * 不应该修改返回的列表.
     * @return 命中路径列表
     */
    public List<UINode> getHitPath() {
        return this.hitPath;
    }

    public boolean isEmpty() {
        return this.hitPath.isEmpty();
    }

    public int size() {
        return this.hitPath.size();
    }


    public static UIHitResult createEmpty() {
        return new UIHitResult(new ArrayList<>());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UIHitResult{");
        for (UINode node : this.hitPath) {
            sb.append(node.getClass().getSimpleName()).append("->");
        }
        if (!this.hitPath.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }

    public void reverse() {
        Collections.reverse(this.hitPath);
    }
}
