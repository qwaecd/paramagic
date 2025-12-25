package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.PlatformNetworking;

public class Networking {
    public static final String PROTOCOL_VERSION = "1";
    private static PlatformNetworking NETWORKING = null;

    public static void init(PlatformNetworking networking) {
        if (NETWORKING != null) {
            return;
        }
        NETWORKING = networking;
        registerAll(networking);
    }

    private static void registerAll(PlatformNetworking networking) {
    }

    public static PlatformNetworking get() {
        if (NETWORKING == null) {
            throw new IllegalStateException("Networking not initialized yet!");
        }
        return NETWORKING;
    }
}
