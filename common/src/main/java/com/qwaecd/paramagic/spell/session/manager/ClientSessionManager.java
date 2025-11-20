package com.qwaecd.paramagic.spell.session.manager;

public class ClientSessionManager implements ISessionManager {
    private static ClientSessionManager INSTANCE;

    public static ClientSessionManager instance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientSessionManager();
        }
        return INSTANCE;
    }
}
