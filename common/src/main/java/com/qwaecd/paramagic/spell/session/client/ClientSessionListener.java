package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.listener.ISpellPhaseListener;

public interface ClientSessionListener extends ISpellPhaseListener {
    void bind(ClientSessionView view);
}
