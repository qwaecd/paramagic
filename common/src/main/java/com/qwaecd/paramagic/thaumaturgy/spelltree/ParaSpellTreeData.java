package com.qwaecd.paramagic.thaumaturgy.spelltree;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParaSpellTreeData implements IDataSerializable {
    private static final String ROOT_KEY = "root";
    private static final String VERSION_KEY = "version";
    private static final String NEXT_NODE_SEQ_KEY = "nextNodeSeq";

    public static final int CURRENT_VERSION = 1;

    @Nonnull
    private final SpellNodeData root;
    private int version;
    private int nextNodeSeq;

    public ParaSpellTreeData() {
        this(new SpellNodeData("n1"), CURRENT_VERSION, 2);
    }

    public ParaSpellTreeData(@Nonnull SpellNodeData root, int version, int nextNodeSeq) {
        this.root = Objects.requireNonNull(root, "root");
        this.version = version;
        this.nextNodeSeq = Math.max(1, nextNodeSeq);
    }

    @Nonnull
    public static ParaSpellTreeData empty() {
        return new ParaSpellTreeData();
    }

    @Nonnull
    public SpellNodeData getRoot() {
        return this.root;
    }

    public int getVersion() {
        return this.version;
    }

    public int getNextNodeSeq() {
        return this.nextNodeSeq;
    }

    @Nonnull
    public SpellNodeData addNode(@Nonnull String parentNodeId, int childIndex, @Nullable ParaOpId operatorId) {
        SpellNodeData parent = this.requireNode(parentNodeId);
        SpellNodeData child = new SpellNodeData(this.nextNodeId(), operatorId, List.of());
        parent.addChild(childIndex, child);
        this.bumpVersion();
        return child;
    }

    public boolean deleteSubtree(@Nonnull String nodeId, @Nonnull List<SpellNodeData> removedNodes) {
        if (this.root.getNodeId().equals(nodeId)) {
            return false;
        }
        SpellNodeData parent = this.findParentOf(nodeId);
        if (parent == null) {
            return false;
        }
        SpellNodeData node = this.findDirectChild(parent, nodeId);
        if (node == null) {
            return false;
        }
        node.forEachSubtreeNode(removedNodes::add);
        parent.removeChild(node);
        this.bumpVersion();
        return true;
    }

    public boolean setOperator(@Nonnull String nodeId, @Nullable ParaOpId operatorId) {
        SpellNodeData node = this.findNode(nodeId);
        if (node == null) {
            return false;
        }
        node.setOperatorId(operatorId);
        this.bumpVersion();
        return true;
    }

    @Nullable
    public SpellNodeData findNode(@Nonnull String nodeId) {
        return this.findNode(this.root, nodeId);
    }

    @Nonnull
    public SpellNodeData requireNode(@Nonnull String nodeId) {
        SpellNodeData node = this.findNode(nodeId);
        if (node == null) {
            throw new IllegalArgumentException("Unknown spell tree node: " + nodeId);
        }
        return node;
    }

    @Nonnull
    private String nextNodeId() {
        return "n" + this.nextNodeSeq++;
    }

    private void bumpVersion() {
        this.version++;
    }

    @Nullable
    private SpellNodeData findNode(@Nonnull SpellNodeData current, @Nonnull String nodeId) {
        if (current.getNodeId().equals(nodeId)) {
            return current;
        }
        for (SpellNodeData child : current.getChildren()) {
            SpellNodeData found = this.findNode(child, nodeId);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    @Nullable
    private SpellNodeData findParentOf(@Nonnull String nodeId) {
        return this.findParentOf(this.root, nodeId);
    }

    @Nullable
    private SpellNodeData findParentOf(@Nonnull SpellNodeData current, @Nonnull String nodeId) {
        if (this.findDirectChild(current, nodeId) != null) {
            return current;
        }
        for (SpellNodeData child : current.getChildren()) {
            SpellNodeData parent = this.findParentOf(child, nodeId);
            if (parent != null) {
                return parent;
            }
        }
        return null;
    }

    @Nullable
    private SpellNodeData findDirectChild(@Nonnull SpellNodeData parent, @Nonnull String nodeId) {
        for (SpellNodeData child : parent.getChildren()) {
            if (child.getNodeId().equals(nodeId)) {
                return child;
            }
        }
        return null;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeObject(ROOT_KEY, this.root);
        codec.writeInt(VERSION_KEY, this.version);
        codec.writeInt(NEXT_NODE_SEQ_KEY, this.nextNodeSeq);
    }

    @Nonnull
    public static ParaSpellTreeData fromCodec(DataCodec codec) {
        SpellNodeData root = codec.readObject(ROOT_KEY, SpellNodeData::fromCodec);
        int version = codec.readInt(VERSION_KEY);
        int nextNodeSeq = codec.readInt(NEXT_NODE_SEQ_KEY);
        return new ParaSpellTreeData(root, version, nextNodeSeq);
    }

    @Nonnull
    public List<SpellNodeData> collectSubtree(@Nonnull String nodeId) {
        SpellNodeData node = this.requireNode(nodeId);
        List<SpellNodeData> nodes = new ArrayList<>();
        node.forEachSubtreeNode(nodes::add);
        return nodes;
    }
}
