package com.qwaecd.paramagic.feature;

import com.qwaecd.paramagic.api.IMagicMap;

import java.util.ArrayList;
import java.util.List;

public class SpellNode {
    private final IMagicMap magicMap;
    private final List<SpellNode> children;
    private final int executionDelay;
    private boolean executed;

    public SpellNode(IMagicMap magicMap, int executionDelay) {
        this.magicMap = magicMap;
        this.executionDelay = executionDelay;
        this.children = new ArrayList<>();
        this.executed = false;
    }

    public void addChild(SpellNode child) {
        children.add(child);
    }

    public IMagicMap getMagicMap() { return magicMap; }
    public List<SpellNode> getChildren() { return children; }
    public int getExecutionDelay() { return executionDelay; }
    public boolean isExecuted() { return executed; }
    public void setExecuted(boolean executed) { this.executed = executed; }
}
