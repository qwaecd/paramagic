package com.qwaecd.paramagic.ui.util;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.inventory.C2SAddSpellTreeNodePacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SClickTreeNodePacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SDeleteSpellTreeSubtreePacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SSetSpellTreeNodeOperatorPacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SSubmitEditedParaDataPacket;
import com.qwaecd.paramagic.network.packet.inventory.SetOperatorAction;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaEditCache;

import javax.annotation.Nonnull;

public final class UINetwork {
    private UINetwork() {}
    public static void sendClickTreeNode(String nodePath) {
        Networking.get().sendToServer(new C2SClickTreeNodePacket(nodePath));
    }

    public static void sendSubmitEditedParaData(@Nonnull ParaData paraData, @Nonnull ParaEditCache.SubmissionMarker marker) {
        Networking.get().sendToServer(new C2SSubmitEditedParaDataPacket(paraData, marker.cacheToken(), marker.modificationVersion()));
    }

    public static void sendAddSpellTreeNode(
            int version,
            @Nonnull String parentNodeId,
            int childIndex,
            boolean useCarriedOperator
    ) {
        Networking.get().sendToServer(new C2SAddSpellTreeNodePacket(
                version,
                parentNodeId,
                childIndex,
                useCarriedOperator
        ));
    }

    public static void sendDeleteSpellTreeSubtree(int version, @Nonnull String nodeId) {
        Networking.get().sendToServer(new C2SDeleteSpellTreeSubtreePacket(version, nodeId));
    }

    public static void sendSetSpellTreeNodeOperator(
            int version,
            @Nonnull String nodeId,
            @Nonnull SetOperatorAction action
    ) {
        Networking.get().sendToServer(new C2SSetSpellTreeNodeOperatorPacket(version, nodeId, action));
    }
}
