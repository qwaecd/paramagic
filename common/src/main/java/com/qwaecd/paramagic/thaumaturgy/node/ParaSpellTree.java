package com.qwaecd.paramagic.thaumaturgy.node;

import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.thaumaturgy.spelltree.ParaSpellTreeData;
import com.qwaecd.paramagic.thaumaturgy.spelltree.SpellNodeData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ParaSpellTree {
    @Nonnull
    private final SpellNode root;

    public ParaSpellTree(@Nonnull ParaSpellTreeData data) {
        this.root = this.resolveNode(data.getRoot(), null);
    }

    @Nonnull
    public SpellNode getRoot() {
        return this.root;
    }

    @Nonnull
    private SpellNode resolveNode(@Nonnull SpellNodeData data, @Nullable SpellNode parent) {
        ParaOperator operator = data.getOperatorId() == null ? null : AllParaOperators.createOperator(data.getOperatorId());
        SpellNode node = new SpellNode(data.getNodeId(), operator, parent);
        for (SpellNodeData childData : data.getChildren()) {
            node.addChild(this.resolveNode(childData, node));
        }
        return node;
    }
}
