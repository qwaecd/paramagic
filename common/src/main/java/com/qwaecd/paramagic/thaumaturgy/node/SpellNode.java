package com.qwaecd.paramagic.thaumaturgy.node;

import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SpellNode {
    @Nonnull
    private final String nodeId;
    @Nullable
    private final ParaOperator operator;
    @Nullable
    private final SpellNode parent;
    @Nonnull
    private final List<SpellNode> children = new ArrayList<>();

    public SpellNode(@Nonnull String nodeId, @Nullable ParaOperator operator, @Nullable SpellNode parent) {
        this.nodeId = Objects.requireNonNull(nodeId, "nodeId");
        this.operator = operator;
        this.parent = parent;
    }

    @Nonnull
    public String getNodeId() {
        return this.nodeId;
    }

    @Nullable
    public ParaOperator getOperator() {
        return this.operator;
    }

    @Nullable
    public SpellNode getParent() {
        return this.parent;
    }

    @Nonnull
    public List<SpellNode> getChildren() {
        return this.children;
    }

    public void addChild(@Nonnull SpellNode child) {
        this.children.add(child);
    }
}
