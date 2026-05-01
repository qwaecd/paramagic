package com.qwaecd.paramagic.spell.builtin.explostion;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.builtin.client.BuiltinSpellVisual;
import com.qwaecd.paramagic.spell.core.SpellSessionRef;
import com.qwaecd.paramagic.spell.client.ClientSession;


@PlatformScope(PlatformScopeType.CLIENT)
public class ExplosionSpellVisual implements BuiltinSpellVisual {
    @Override
    public void onCreateSession(ClientSession session, SpellSessionRef ref) {
    }
}
