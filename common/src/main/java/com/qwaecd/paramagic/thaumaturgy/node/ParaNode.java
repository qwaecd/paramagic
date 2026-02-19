package com.qwaecd.paramagic.thaumaturgy.node;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@PlatformScope(PlatformScopeType.COMMON)
public class ParaNode {
    @Nonnull
    private final String componentId;

    @Nullable
    private final ParaNode parent;
    @Nonnull
    private List<ParaNode> children;

    /**
     * 同层的兄弟节点列表，包含自己
     */
    private List<ParaNode> siblings;

    @Nullable
    private ParaOperator operator;

    @Nonnull
    private NodeState state = NodeState.PENDING;

    public ParaNode(@Nonnull String componentId, @Nullable ParaNode parent) {
        this.componentId = componentId;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    @Nonnull
    public String getId() {
        return this.componentId;
    }

    @Nonnull
    public String getNodePath() {
        return this.componentId;
    }

    @Nullable
    public ParaOperator getOperator() {
        return this.operator;
    }

    public void setOperator(@Nullable ParaOperator operator) {
        this.operator = operator;
    }

    @Nullable
    public ParaNode getParent() {
        return this.parent;
    }

    @Nonnull
    public final List<ParaNode> getChildren() {
        return this.children;
    }

    @Nonnull
    public final List<ParaNode> getSiblings() {
        if (this.siblings == null) {
            if (this.parent == null) {
                this.siblings = List.of(this);
            } else {
                this.siblings = this.parent.getChildren();
            }
        }
        return this.siblings;
    }

    @Nonnull
    public NodeState getState() {
        return this.state;
    }

    public void setState(@Nonnull NodeState state) {
        this.state = state;
    }

    final void addChild(@Nonnull ParaNode child) {
        this.children.add(child);
    }

    final void freeze() {
        this.children = List.copyOf(this.children);
    }
}
