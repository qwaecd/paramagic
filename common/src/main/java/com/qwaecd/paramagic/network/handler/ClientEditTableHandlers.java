package com.qwaecd.paramagic.network.handler;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.packet.inventory.S2CSubmitEditedParaDataResultPacket;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaEditCache;

@PlatformScope(PlatformScopeType.CLIENT)
public final class ClientEditTableHandlers {
    private ClientEditTableHandlers() {}

    public static void submitEditedParaDataResult(S2CSubmitEditedParaDataResultPacket packet, NetworkContext context) {
        if (packet.isSuccess()) {
            ParaEditCache.markSubmittedIfMatches(packet.getCacheToken(), packet.getCacheVersion());
            Paramagic.LOG.debug("Edited ParaData submission confirmed by server for cacheToken={}, version={}.",
                    packet.getCacheToken(), packet.getCacheVersion());
            return;
        }
        ParaEditCache.clearPendingSubmissionIfMatches(packet.getCacheToken(), packet.getCacheVersion());
        Paramagic.LOG.warn("Edited ParaData submission was rejected by the server for cacheToken={}, version={}.",
                packet.getCacheToken(), packet.getCacheVersion());
    }
}
