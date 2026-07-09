package com.qwaecd.paramagic.network.packet.inventory;

public enum SpellTreeEditRejectReason {
    STALE_VERSION,
    INVALID_TARGET,
    INVALID_NODE,
    INVALID_RESOURCE,
    PREDICTED_NODE_ID_MISMATCH,
    OPERATION_REJECTED
}
