package com.qwaecd.paramagic.feature;

import com.qwaecd.paramagic.api.IMagicMap;

import java.util.ArrayList;
import java.util.List;

public class ManaNode {
    private final IMagicMap magicMap;
    private final List<ManaNode> children;
    private final int executionDelay;
    private boolean executed;

    public ManaNode(IMagicMap magicMap, int executionDelay) {
        this.magicMap = magicMap;
        this.executionDelay = executionDelay;
        this.children = new ArrayList<>();
        this.executed = false;
    }

    public void addChild(ManaNode child) {
        children.add(child);
    }

    public IMagicMap getMagicMap() {
        return magicMap;
    }

    public List<ManaNode> getChildren() {
        return children;
    }

    public int getExecutionDelay() {
        return executionDelay;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }
}
