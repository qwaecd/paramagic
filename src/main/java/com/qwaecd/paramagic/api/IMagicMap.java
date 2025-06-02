package com.qwaecd.paramagic.api;

public interface IMagicMap {
    void execute(ManaContext context);

    MagicMapType getType();

    String getId();

    int getCastDelay();

    int getManaCost();

    enum MagicMapType {
        RENDER,    // Client-side visual effects
        EXECUTE,   // Server-side world modifications
        INFO       // Data processing only
    }
}
