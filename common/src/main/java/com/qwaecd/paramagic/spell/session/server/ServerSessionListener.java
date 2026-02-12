package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.spell.SpellPhaseListener;

public interface ServerSessionListener extends SpellPhaseListener {
    void bind(ServerSessionView session);
    default void onSessionClose() {}
}
