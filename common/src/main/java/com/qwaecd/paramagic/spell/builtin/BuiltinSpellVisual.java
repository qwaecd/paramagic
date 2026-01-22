package com.qwaecd.paramagic.spell.builtin;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.SpellIdentifier;
import com.qwaecd.paramagic.spell.session.SpellSessionRef;
import com.qwaecd.paramagic.spell.session.client.ClientSession;

@PlatformScope(PlatformScopeType.CLIENT)
public interface BuiltinSpellVisual {
    SpellIdentifier getSpellId();
    /**
     * 在 clientSession 创建时调用.
     * @param session 创建的 ClientSession
     */
    void applyVisual(ClientSession session, SpellSessionRef ref);

    default void onClose(ClientSession session) {}
}
