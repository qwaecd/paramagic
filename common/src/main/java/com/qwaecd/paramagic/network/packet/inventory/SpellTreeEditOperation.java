package com.qwaecd.paramagic.network.packet.inventory;

/** A user intent emitted by the wand spell-tree UI. */
public enum SpellTreeEditOperation {
    ADD_CHILD,
    DELETE_SUBTREE,
    CLEAR_CHILDREN,
    INTERACT_NODE_OPERATOR
}
