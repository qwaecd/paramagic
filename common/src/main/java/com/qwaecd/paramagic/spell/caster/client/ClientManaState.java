package com.qwaecd.paramagic.spell.caster.client;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

/** Client-only display cache populated exclusively by S2C mana snapshots. */
@PlatformScope(PlatformScopeType.CLIENT)
public final class ClientManaState {
    private static int mana;
    private static int maxMana;

    private ClientManaState() {
    }

    public static int getMana() {
        return mana;
    }

    public static int getMaxMana() {
        return maxMana;
    }

    public static void update(int mana, int maxMana) {
        ClientManaState.maxMana = Math.max(0, maxMana);
        ClientManaState.mana = Math.max(0, Math.min(mana, ClientManaState.maxMana));
    }

    public static void reset() {
        mana = 0;
        maxMana = 0;
    }
}
