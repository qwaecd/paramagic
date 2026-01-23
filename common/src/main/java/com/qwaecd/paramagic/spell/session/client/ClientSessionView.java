package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.session.store.SessionDataStore;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;

public interface ClientSessionView {
    CasterTransformSource casterSource();
    int casterNetId() throws NullPointerException;
    SessionDataStore getDataStore();
}
