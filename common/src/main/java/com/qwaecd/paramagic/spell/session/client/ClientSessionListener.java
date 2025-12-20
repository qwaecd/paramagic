package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.listener.SpellPhaseListener;

public interface ClientSessionListener extends SpellPhaseListener {
    void bind(ClientSessionView view);
    default void onSessionClose() {}
}
