package com.qwaecd.paramagic.network.handler;

import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.packet.session.S2CSessionDataSyncPacket;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.session.client.ClientSession;
import com.qwaecd.paramagic.spell.session.client.ClientSessionManager;
import com.qwaecd.paramagic.spell.session.store.SessionDataStore;
import com.qwaecd.paramagic.spell.session.store.SessionDataSyncPayload;


@PlatformScope(PlatformScopeType.CLIENT)
public class ClientSessionDataHandlers {
    public static void syncSessionData(S2CSessionDataSyncPacket packet, NetworkContext context) {
        SessionDataSyncPayload syncData = packet.getSyncData();
        ClientSessionManager manager = ClientSessionManager.instance();
        ClientSession session = (ClientSession) manager.getSession(syncData.sessionId);
        if (session == null) {
            return;
        }
        final SessionDataStore dataStore = session.getDataStore();
        syncData.getEntries().forEach(entry -> dataStore.setDataValueWithSeq(entry.dataId, entry.sequenceNumber, entry.value));
    }
}
